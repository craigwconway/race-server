package com.bibsmobile.controller;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.CartUtil;
import net.authorize.sim.Fingerprint;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RequestMapping("/carts")
@Controller
@RooWebScaffold(path = "carts", formBackingObject = Cart.class)
@RooWebJson(jsonObject = Cart.class)
public class CartController {

    @RequestMapping(value = "/item/{id}", produces = "text/html")
    public String view(@PathVariable("id") Long eventCartItemId) {
        return "cart";
    }

    @RequestMapping(value = "/item/{id}/updatequantity", produces = "text/html")
    public String updateItemQuantity(@PathVariable("id") Long eventCartItemId, @RequestParam Integer quantity, Model uiModel) {
        Cart cart = CartUtil.updateOrCreateCart(eventCartItemId, quantity);

        //setupPaymentForm(uiModel, cart, true);

        uiModel.addAttribute("cart", cart);
        return "redirect:/carts/item/" + eventCartItemId;
    }



    private void setupPaymentForm(Model uiModel, Cart cart, boolean test) {

        String apiLoginId = "7rWKZe476";
        uiModel.addAttribute("apiLoginId", apiLoginId);

        String transactionKey = (test) ? "5Fg6846nb7pAS4X4" : "" + cart.getId();
        System.out.println(transactionKey + " transactionKey");
        uiModel.addAttribute("transactionKey", transactionKey);

        String relayResponseUrl = "http:/localhost:8080/bibs-server/events/registrationComplete?event=1";
        uiModel.addAttribute("relayResponseUrl", relayResponseUrl);

        double amount = (test) ? new Random().nextDouble() + .01 : cart.getTotal();

        NumberFormat df = DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        String samount = df.format(amount);
        uiModel.addAttribute("amount", samount);

        Fingerprint fingerprint = Fingerprint.createFingerprint(
                apiLoginId,
                transactionKey,
                1234567890,  // random sequence used for creating the finger print
                samount);

        long x_fp_sequence = fingerprint.getSequence();
        uiModel.addAttribute("x_fp_sequence", x_fp_sequence);

        long x_fp_timestamp = fingerprint.getTimeStamp();
        uiModel.addAttribute("x_fp_timestamp", x_fp_timestamp);

        String x_fp_hash = fingerprint.getFingerprintHash();
        uiModel.addAttribute("x_fp_hash", x_fp_hash);

    }
}