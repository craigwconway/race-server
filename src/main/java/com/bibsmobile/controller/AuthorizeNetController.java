package com.bibsmobile.controller;

import com.bibsmobile.model.AuthorizeData;
import com.bibsmobile.model.Cart;
import net.authorize.sim.Fingerprint;
import net.authorize.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @RequestMapping(value = "/cartdata/{cart}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonAuthorizeNetCart(HttpServletRequest request, @PathVariable("cart") Long cartId) {
        Cart cart = Cart.findCart(cartId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        boolean test = true;
        if (cart == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        String apiLoginId = "7rWKZe476";

        String transactionKey = (test) ? "5Fg6846nb7pAS4X4" : "" + cart.getId();

        String relayResponseUrl = getURLWithContextPath(request)+ "/authorize/response";

        NumberFormat df = DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        String amount = df.format(cart.getTotal());

        Fingerprint fingerprint = Fingerprint.createFingerprint(
                apiLoginId,
                transactionKey,
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
        data.setXLogin(StringUtils.sanitizeString(apiLoginId));
        data.setXRelayUrl(relayResponseUrl);

        return new ResponseEntity<>(data.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping("/response")
    public void getAuthorizeResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("http://google.com");
    }

    private String getURLWithContextPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

}
