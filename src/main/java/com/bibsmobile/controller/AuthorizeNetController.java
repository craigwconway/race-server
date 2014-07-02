package com.bibsmobile.controller;

import com.bibsmobile.model.AuthorizeData;
import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.CartUtil;

import net.authorize.ResponseField;
import net.authorize.sim.Fingerprint;
import net.authorize.sim.Result;
import net.authorize.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@RequestMapping("/authorize")
@Controller
public class AuthorizeNetController {

    @Value("${authorize.net.api.login.id}")
    private String apiLoginId;

    @Value("${authorize.net.transaction.sequence}")
    private long transactionSequence;

    @Value("${authorize.net.transaction.key}")
    private String transactionKey;

    @Value("${authorize.net.transaction.relay-response-url}")
    private String relayResponseUrl;

    @Value("${authorize.net.transaction.redirect-base-url}")
    private String redirectBaseUrl;

    @Autowired
    private SimpleMailMessage registrationMessage;

    @Autowired
    private JavaMailSenderImpl mailSender;

    @RequestMapping(value = "/cartdata", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonAuthorizeNetCart(HttpServletRequest request) {
        Long cartIdFromSession = (Long) request.getSession().getAttribute(CartUtil.SESSION_ATTR_CART_ID);
        Cart cart = null;
        if (cartIdFromSession != null) {
            cart = Cart.findCart(cartIdFromSession);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (cart == null || cart.getStatus() != cart.NEW) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }

        NumberFormat df = DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        String amount = df.format(cart.getTotal());

        Fingerprint fingerprint = Fingerprint.createFingerprint(
                apiLoginId,
                transactionKey,
                transactionSequence,
                amount);

        long x_fp_sequence = fingerprint.getSequence();

        long x_fp_timestamp = fingerprint.getTimeStamp();

        String x_fp_hash = fingerprint.getFingerprintHash();

        String x_invoice_num = cart.getId().toString();

        AuthorizeData data = new AuthorizeData();
        data.setAmount(amount);
        data.setXFpHash(StringUtils.sanitizeString(x_fp_hash));
        data.setXFpSequence(StringUtils.sanitizeString(Long.toString(x_fp_sequence)));
        data.setXFpTimestamp(StringUtils.sanitizeString(Long.toString(x_fp_timestamp)));
        data.setXLogin(StringUtils.sanitizeString(apiLoginId));
        data.setXRelayUrl(relayResponseUrl);
        data.setXInvoiceNum(StringUtils.sanitizeString(x_invoice_num));

        return new ResponseEntity<>(data.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping("/response")
    public String getAuthorizeResponse(HttpServletRequest request, Model model) throws IOException {
        Result result = Result.createResult(apiLoginId, apiLoginId, request.getParameterMap());
        String redirectUrl;
        if (result == null) {
            redirectUrl = redirectErrorUrl();

        } else if (result.isApproved()) {
            redirectUrl = redirectTransactionSuccessUrl(result);
            String cartIdStr = org.apache.commons.lang3.StringUtils.trimToEmpty(result.getResponseMap().get(ResponseField.TRANSACTION_ID.getFieldName()));
            Cart cart = Cart.findCart(Long.valueOf(cartIdStr));
            if (cart != null) {
                cart.setStatus(Cart.COMPLETE);
                cart.merge();
                for (CartItem cartItem : cart.getCartItems()) {
                    UserProfile userProfile = cartItem.getUserProfile();
                    if (userProfile != null && org.apache.commons.lang3.StringUtils.isNotEmpty(userProfile.getEmail())) {
                    	try{
            	            registrationMessage.setTo(userProfile.getEmail());
            	            mailSender.send(registrationMessage);
                    	}catch(Exception e){
                    		System.out.println("EXCEPTION: Email Send Fail - "+e.getMessage());
                    	}
                    }
                }
                request.getSession().removeAttribute(CartUtil.SESSION_ATTR_CART_ID);
                for (CartItem cartItem : cart.getCartItems()) {
                    cartItem.getEventCartItem().setPurchased(cartItem.getEventCartItem().getPurchased() + cartItem.getQuantity());
                    cartItem.getEventCartItem().persist();
                }
            }
        } else {
            redirectUrl = redirectTransactionFailUrl(result);
        }

        model.addAttribute("redirectUrl", redirectUrl);
        return "jsRedirect";
    }

    private String redirectErrorUrl() throws IOException {
        StringBuilder redirectUrl = new StringBuilder();
        redirectUrl.append(redirectBaseUrl);
        redirectUrl.append("?error=1");
        return redirectUrl.toString();
    }

    private String redirectTransactionSuccessUrl(Result result) throws IOException {
        StringBuilder redirectUrl = new StringBuilder();

        redirectUrl.append(redirectBaseUrl);

        String transactionId = StringUtils.sanitizeString(org.apache.commons.lang3.StringUtils.trimToEmpty(result.getResponseMap().get(ResponseField.TRANSACTION_ID.getFieldName())));

        redirectUrl.append("?response_code=1&transaction_id=<transaction_id_from_authorize_net>".
                replace("<transaction_id_from_authorize_net>", transactionId));

        return redirectUrl.toString();
    }

    private String redirectTransactionFailUrl(Result result) throws IOException {
        StringBuilder redirectUrl = new StringBuilder();

        redirectUrl.append(redirectBaseUrl);

        String responseCode = StringUtils.sanitizeString(org.apache.commons.lang3.StringUtils.trimToEmpty(result.getResponseMap().get(ResponseField.RESPONSE_CODE.getFieldName())));
        String transactionId = StringUtils.sanitizeString(org.apache.commons.lang3.StringUtils.trimToEmpty(result.getResponseMap().get(ResponseField.TRANSACTION_ID.getFieldName())));
        String responseReasonText = StringUtils.sanitizeString(org.apache.commons.lang3.StringUtils.trimToEmpty(result.getResponseMap().get(ResponseField.RESPONSE_REASON_TEXT.getFieldName())));
        String responseReasonCode = StringUtils.sanitizeString(org.apache.commons.lang3.StringUtils.trimToEmpty(result.getResponseMap().get(ResponseField.RESPONSE_REASON_CODE.getFieldName())));

        redirectUrl.append("?response_code=<response_code_from_authorize_net>&transaction_id=<transaction_id_from_authorize_net>&response_reason_text=<response_reason_text_from_authorize_net>&response_reason_code=<response_reason_code_from_authorize_net>".
                replace("<response_code_from_authorize_net>", responseCode).
                replace("<transaction_id_from_authorize_net>", transactionId).
                replace("<response_reason_text_from_authorize_net>", responseReasonText).
                replace("<response_reason_code_from_authorize_net>", responseReasonCode));

        return redirectUrl.toString();
    }

}
