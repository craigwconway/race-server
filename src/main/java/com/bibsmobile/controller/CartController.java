package com.bibsmobile.controller;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.UserProfile;
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

    @RequestMapping(value = "/item/{id}/updatequantity", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> updateOrCreateCartJson(@PathVariable("id") Long eventCartItemId, @RequestParam Integer eventCartItemQuantity) {
        Cart cart = updateOrCreateCart(eventCartItemId, eventCartItemQuantity);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (cart == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cart.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/item/{id}/updatequantity", produces = "text/html")
    public String updateItemQuantity(@PathVariable("id") Long eventCartItemId, @RequestParam Integer quantity, Model uiModel) {
        Cart cart = updateOrCreateCart(eventCartItemId, quantity);

        //setupPaymentForm(uiModel, cart, true);

        uiModel.addAttribute("cart", cart);
        return "redirect:/carts/item/" + eventCartItemId;
    }

    private Cart updateOrCreateCart(Long eventCartItemId, Integer quantity) {
        if (quantity < 0) {
            quantity = 0;
        }
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        UserProfile user = UserProfile.findUserProfilesByUsernameEquals(
                username).getSingleResult();
        Cart cart = null;
        try {
            List<Cart> carts = Cart.findCartsByUser(user).getResultList();
            for (Cart c : carts) {
                if (c.getStatus() == Cart.NEW) {
                    cart = c;
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR ADDING TO CART");
        }

        Date now = new Date();

        //create cart if it doesn't exist yet.
        if (cart == null) {
            cart = new Cart();
            cart.setStatus(Cart.NEW);
            cart.setCreated(now);
            cart.setUpdated(now);
            cart.setUser(user);
            cart.persist();
        }

        EventCartItem i = EventCartItem.findEventCartItem(eventCartItemId);
        CartItem cartItem;
        //if doesn't have product in cart and adding new not removing
        if (CollectionUtils.isEmpty(cart.getCartItems()) && quantity > 0) {
            cartItem = getCartItem(cart, now, i);
            cartItem.persist();
            cart.getCartItems().add(cartItem);
        } else {
            boolean existedCartItem = false;
            EventCartItem eventCartItem;
            for (CartItem ci : cart.getCartItems()) {
                eventCartItem = ci.getEventCartItem();
                //if product already exists in cart
                if (eventCartItem.getId().equals(eventCartItemId)) {
                    //adding
                    if (quantity > 0) {

                        //>0 increasing, <0 decreasing
                        int diff = quantity - ci.getQuantity();
                        //only if increasing
                        if (diff > eventCartItem.getAvailable()) {
                            quantity = eventCartItem.getAvailable();
                        }
                        ci.setQuantity(quantity);
                        ci.setUpdated(now);
                        ci.persist();
                        eventCartItem.setAvailable(eventCartItem.getAvailable() - diff);
                        eventCartItem.persist();
                    }
                    //removing
                    else {
                        ci.remove();
                    }
                    existedCartItem = true;
                    break;
                }
            }
            if (!existedCartItem && quantity > 0) {
                cartItem = getCartItem(cart, now, i);
                cartItem.persist();
                cart.getCartItems().add(cartItem);
            }
        }

        double total = 0;
        for (CartItem ci : cart.getCartItems()) {
            total += (ci.getQuantity() * ci.getEventCartItem().getActualPrice());
        }
        cart.setTotal(total);
        cart.merge();
        return cart;
    }

    private CartItem getCartItem(Cart cart, Date now, EventCartItem i) {
        CartItem cartItem;
        cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setEventCartItem(i);
        cartItem.setQuantity(1);
        cartItem.setCreated(now);
        cartItem.setUpdated(now);
        return cartItem;
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