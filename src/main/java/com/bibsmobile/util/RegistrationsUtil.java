package com.bibsmobile.util;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.UserProfile;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class RegistrationsUtil {
    private RegistrationsUtil() {
        super();
    }

    public static CartItem findCartItemFromInvoice(String invoiceId, String firstName, String email) {
        // parse invoice ID
        Tuple<Cart, String> tmpInvoiceIdParsed = parseInvoiceId(invoiceId);
        Event event = tmpInvoiceIdParsed.getU().getEvent();
        String stripeToken = tmpInvoiceIdParsed.getV();

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

    public static List<Cart> search(Event event, String firstName, String lastName, String email, String invoiceId) {
        boolean firstNameGiven = (firstName != null && !firstName.isEmpty());
        boolean lastNameGiven = (lastName != null && !lastName.isEmpty());
        boolean emailGiven = (email != null && !email.isEmpty());
        boolean invoiceIdGiven = (invoiceId != null && !invoiceId.isEmpty());
        if (!(firstNameGiven || lastNameGiven || emailGiven || invoiceIdGiven))
            return null;

        // invoice ID parsing
        String stripeToken = null;
        if (invoiceIdGiven) {
            Tuple<Cart, String> tmpInvoiceIdParsed = parseInvoiceId(invoiceId);
            if (tmpInvoiceIdParsed == null)
                invoiceIdGiven = false;
            else {
                if (!event.getId().equals(tmpInvoiceIdParsed.getU().getEvent().getId())) invoiceIdGiven = false;
                stripeToken = tmpInvoiceIdParsed.getV();
            }
        }

        // get relevant carts for event
        Set<Cart> carts = new HashSet<>();
        List<EventCartItem> eventCartItems = EventCartItem.findEventCartItemsByEvent(event).getResultList();
        if (!eventCartItems.isEmpty()) {
            List<CartItem> cartItems = CartItem.findCompletedCartItemsByEventCartItems(eventCartItems, true).getResultList();
            for (CartItem cartItem : cartItems) {
                carts.add(cartItem.getCart());
            }
        }

        // filter carts by first & last name
        List<Cart> registrations = new LinkedList<>();
        for (Cart c : carts) {
            boolean matchFirstName = false;
            boolean matchLastName = false;
            boolean matchEmail = false;
            boolean matchInvoiceId = false;

            // check stripe token on cart
            if (invoiceIdGiven)
                matchInvoiceId = c.getStripeChargeId() != null && c.getStripeChargeId().equalsIgnoreCase(stripeToken);

            // check user data on cart items
            for (CartItem ci : c.getCartItems()) {
                // check User Profile
                if (firstNameGiven)
                    matchFirstName = ci.getUserProfile().getFirstname() != null && ci.getUserProfile().getFirstname().equalsIgnoreCase(firstName);
                else if (lastNameGiven)
                    matchLastName = ci.getUserProfile().getLastname() != null && ci.getUserProfile().getLastname().equalsIgnoreCase(lastName);
                else if (emailGiven)
                    matchEmail = ci.getUserProfile().getEmail() != null && ci.getUserProfile().getEmail().equalsIgnoreCase(email);

                // check team members
                if (ci.getTeam() != null) {
                    for (UserProfile teamMember : ci.getTeam().getMembers()) {
                        if (firstNameGiven)
                            matchFirstName = teamMember.getFirstname() != null && teamMember.getFirstname().equalsIgnoreCase(firstName);
                        else if (lastNameGiven)
                            matchLastName = teamMember.getLastname() != null && teamMember.getLastname().equalsIgnoreCase(lastName);
                        else if (emailGiven)
                            matchEmail = teamMember.getEmail() != null && teamMember.getEmail().equalsIgnoreCase(email);
                    }
                }
            }

            if (matchFirstName || matchLastName || matchEmail || matchInvoiceId)
                registrations.add(c);
        }

        return registrations;
    }

    private static Tuple<Cart, String> parseInvoiceId(String invoiceId) {
        // invoiceID schema: "B" + eventID + "T" + stripeToken
        int splitter = invoiceId.indexOf('T');
        if (splitter == -1) return null;
        Long cartId = Long.valueOf(invoiceId.substring(1, splitter));
        System.out.println("Cart ID: " + cartId);
        String stripeToken = invoiceId.substring(splitter + 1);
        Cart cart = Cart.findCart(cartId);
        if (cart == null) return null;
        if (cart.getStatus() == Cart.REFUNDED) return null;
        return new Tuple<>(cart, stripeToken);
    }

    private static class Tuple<U, V> {
        private final U u;
        private final V v;

        protected Tuple(U u, V v) {
            super();
            this.u = u;
            this.v = v;
        }

        public U getU() {
            return u;
        }

        public V getV() {
            return v;
        }
    }
}
