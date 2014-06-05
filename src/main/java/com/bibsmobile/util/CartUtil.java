package com.bibsmobile.util;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemTypeEnum;
import com.bibsmobile.model.UserProfile;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Created by Jevgeni on 4.06.2014.
 */
public class CartUtil {
    public static Cart updateOrCreateCart(Long eventCartItemId, Integer quantity, UserProfile userProfile) {
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
            cartItem = getCartItem(cart, now, i, userProfile);
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
                cartItem = getCartItem(cart, now, i, userProfile);
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

    private static CartItem getCartItem(Cart cart, Date now, EventCartItem i, UserProfile userProfile) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setEventCartItem(i);
        cartItem.setQuantity(1);
        cartItem.setCreated(now);
        cartItem.setUpdated(now);
        //registration
        if(i.getType() == EventCartItemTypeEnum.TICKET) {
            cartItem.setUserProfile(userProfile);
        }
        return cartItem;
    }

}
