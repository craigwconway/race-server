package com.bibsmobile.util;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemTypeEnum;
import com.bibsmobile.model.UserProfile;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jevgeni on 4.06.2014.
 */
public class CartUtil {

    public static final String SESSION_ATTR_CART_ID = "cartId";

    public static Cart updateOrCreateCart(HttpSession session, Long eventCartItemId, Integer quantity, UserProfile userProfile, String color, String size) {
        if (quantity < 0) {
            quantity = 0;
        }
        Cart cart = null;
        UserProfile user = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            if (!username.equals("anonymousUser")) {
                user = UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
            }
        }

        Long cartIdFromSession = (Long) session.getAttribute(SESSION_ATTR_CART_ID);

        if (cartIdFromSession != null) {
            cart = Cart.findCart(cartIdFromSession);
        } else if (user != null) {
            try {
                List<Cart> carts = Cart.findCartsByUser(user).getResultList();
                for (Cart c : carts) {
                    if (c.getStatus() == Cart.NEW) {
                        cart = c;
                        session.setAttribute(SESSION_ATTR_CART_ID, cart.getId());
                    }
                }
            } catch (Exception e) {
                System.out.println("ERROR ADDING TO CART");
            }
        }

        Date now = new Date();
        //create cart if it doesn't exist yet.
        if (cart == null) {
            cart = new Cart();
            cart.setStatus(Cart.NEW);
            cart.setCreated(now);
            cart.setUpdated(now);
            cart.setCartItems(new ArrayList<CartItem>());
            //user=null if anonymous
            cart.setUser(user);
            cart.persist();
            session.setAttribute(SESSION_ATTR_CART_ID, cart.getId());
        }

        EventCartItem i = EventCartItem.findEventCartItem(eventCartItemId);
        CartItem cartItem;
        //if doesn't have product in cart and adding new not removing
        if (CollectionUtils.isEmpty(cart.getCartItems()) && quantity > 0) {
            cartItem = getCartItem(cart, now, i, userProfile, quantity, color, size);
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
                cartItem = getCartItem(cart, now, i, userProfile, quantity, color, size);
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

    private static CartItem getCartItem(Cart cart, Date now, EventCartItem i, UserProfile userProfile, Integer quantity, String color, String size) {
        CartItem cartItem = new CartItem();
        if (i.getType() == EventCartItemTypeEnum.T_SHIRT) {
            cartItem.setColor(color);
            cartItem.setSize(size);
        }
        cartItem.setPrice(i.getActualPrice());
        cartItem.setCart(cart);
        cartItem.setEventCartItem(i);
        //add maximum available quantity
        if (i.getAvailable() < quantity) {
            quantity = i.getAvailable();
        }
        cartItem.setQuantity(quantity);
        cartItem.setCreated(now);
        cartItem.setUpdated(now);
        cartItem.setUserProfile(userProfile);

        //updating available quantity in eventcartitem
        i.setAvailable(i.getAvailable() - quantity);
        i.persist();
        return cartItem;
    }

}
