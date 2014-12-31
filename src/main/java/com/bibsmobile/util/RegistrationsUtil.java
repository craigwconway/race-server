package com.bibsmobile.util;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;

import java.util.List;

public final class RegistrationsUtil {
    private RegistrationsUtil() {
        super();
    }

    public static CartItem findCartItemFromInvoice(String invoiceId, String firstName, String email) {
        // invoiceID schema: "B" + eventID + "T" + stripeToken
        int splitter = invoiceId.indexOf('T');
        if (splitter == -1) return null;
        Long eventId = Long.valueOf(invoiceId.substring(1, splitter));
        String stripeToken = invoiceId.substring(splitter + 1);
        Event event = Event.findEvent(eventId);
        if (event == null) return null;

        // get carts by event and filter by stripe token, first name and email
        Cart tmpCart;
        CartItem cartItem = null;
        List<EventCartItem> eventCartItems = EventCartItem.findEventCartItemsByEvent(event).getResultList();
        if (!eventCartItems.isEmpty()) {
            List<CartItem> cartItems = CartItem.findCompletedCartItemsByEventCartItems(eventCartItems, true).getResultList();
            for (CartItem tmpCartItem : cartItems) {
                tmpCart = tmpCartItem.getCart();
                if (tmpCart.getStripeChargeId() != null &&
                        stripeToken.equalsIgnoreCase(tmpCart.getStripeChargeId()) &&
                        tmpCartItem.getUserProfile() != null &&
                        tmpCartItem.getUserProfile().getFirstname() != null &&
                        firstName.equalsIgnoreCase(tmpCartItem.getUserProfile().getFirstname()) &&
                        tmpCartItem.getUserProfile().getEmail() != null &&
                        email.equalsIgnoreCase(tmpCartItem.getUserProfile().getEmail())) {
                    cartItem = tmpCartItem;
                    break;
                }
            }
        }
        return cartItem;
    }

    public static Cart findCartFromInvoice(String invoiceId, String firstName, String email) {
        CartItem ci = findCartItemFromInvoice(invoiceId, firstName, email);
        if (ci == null) return null;
        return ci.getCart();
    }
}
