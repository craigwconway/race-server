package com.bibsmobile.controller;

import com.bibsmobile.model.AuthorizeData;
import com.bibsmobile.model.Cart;
import net.authorize.ResponseField;
import net.authorize.sim.Fingerprint;
import net.authorize.sim.Result;
import net.authorize.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@RequestMapping("/authorize")
@Controller
public class AuthorizeNetController {

    private static final String API_LOGIN_ID = "7rWKZe476";
    private static final String TRANSACTION_TEST_KEY = "5Fg6846nb7pAS4X4";
    private static final boolean TEST_MODE = true;

    @RequestMapping(value = "/cartdata/{cart}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonAuthorizeNetCart(HttpServletRequest request, @PathVariable("cart") Long cartId) {
        Cart cart = Cart.findCart(cartId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (cart == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }

        String trKey = (TEST_MODE) ? TRANSACTION_TEST_KEY : cart.getId().toString();

        String relayResponseUrl = getURLWithContextPath(request) + "/authorize/response";

        NumberFormat df = DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        String amount = df.format(cart.getTotal());

        Fingerprint fingerprint = Fingerprint.createFingerprint(
                API_LOGIN_ID,
                trKey,
                1234567890,
                amount);

        long x_fp_sequence = fingerprint.getSequence();

        long x_fp_timestamp = fingerprint.getTimeStamp();

        String x_fp_hash = fingerprint.getFingerprintHash();

        AuthorizeData data = new AuthorizeData();
        data.setAmount(amount);
        data.setXFpHash(StringUtils.sanitizeString(x_fp_hash));
        data.setXFpSequence(StringUtils.sanitizeString(Long.toString(x_fp_sequence)));
        data.setXFpTimestamp(StringUtils.sanitizeString(Long.toString(x_fp_timestamp)));
        data.setXLogin(StringUtils.sanitizeString(API_LOGIN_ID));
        data.setXRelayUrl(relayResponseUrl);

        return new ResponseEntity<>(data.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping("/response")
    public String getAuthorizeResponse(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        Result result = Result.createResult(API_LOGIN_ID, API_LOGIN_ID, request.getParameterMap());
        String redirectUrl;
        if (result == null) {
            redirectUrl = redirectErrorUrl(request, response);
        } else if (result.isApproved()) {
            redirectUrl = redirectTransactionSuccessUrl(result, request, response);
        } else {
            redirectUrl = redirectTransactionFailUrl(result, request, response);
        }
        model.addAttribute("redirectUrl", redirectUrl);
        return "jsRedirect";
    }

    private String redirectErrorUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder redirectUrl = new StringBuilder();
        String baseUrl = getBaseUrl(request);
        redirectUrl.append(baseUrl);
        redirectUrl.append("/app/registration.html#!/response?error=1");
        return redirectUrl.toString();
    }

    private String redirectTransactionSuccessUrl(Result result, HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder redirectUrl = new StringBuilder();

        String baseUrl = getBaseUrl(request);
        redirectUrl.append(baseUrl);

        String transactionId = StringUtils.sanitizeString(org.apache.commons.lang3.StringUtils.trimToEmpty(result.getResponseMap().get(ResponseField.TRANSACTION_ID.getFieldName())));

        redirectUrl.append("/app/registration.html#!/response?response_code=1&transaction_id=<transaction_id_from_authorize_net>".
                replace("<transaction_id_from_authorize_net>", transactionId));

        return redirectUrl.toString();
    }

    private String redirectTransactionFailUrl(Result result, HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder redirectUrl = new StringBuilder();

        String baseUrl = getBaseUrl(request);
        redirectUrl.append(baseUrl);

        String responseCode = StringUtils.sanitizeString(org.apache.commons.lang3.StringUtils.trimToEmpty(result.getResponseMap().get(ResponseField.RESPONSE_CODE.getFieldName())));
        String transactionId = StringUtils.sanitizeString(org.apache.commons.lang3.StringUtils.trimToEmpty(result.getResponseMap().get(ResponseField.TRANSACTION_ID.getFieldName())));
        String responseReasonText = StringUtils.sanitizeString(org.apache.commons.lang3.StringUtils.trimToEmpty(result.getResponseMap().get(ResponseField.RESPONSE_REASON_TEXT.getFieldName())));
        String responseReasonCode = StringUtils.sanitizeString(org.apache.commons.lang3.StringUtils.trimToEmpty(result.getResponseMap().get(ResponseField.RESPONSE_REASON_CODE.getFieldName())));

        redirectUrl.append("/app/registration.html#!/response?response_code=<response_code_from_authorize_net>&transaction_id=<transaction_id_from_authorize_net>&response_reason_text=<response_reason_text_from_authorize_net>&response_reason_code=<response_reason_code_from_authorize_net>".
                replace("<response_code_from_authorize_net>", responseCode).
                replace("<transaction_id_from_authorize_net>", transactionId).
                replace("<response_reason_text_from_authorize_net>", responseReasonText).
                replace("<response_reason_code_from_authorize_net>", responseReasonCode));

        return redirectUrl.toString();
    }

    private String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    private String getURLWithContextPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

}
