package com.bibsmobile.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bibsmobile.job.BaseJob;
import com.bibsmobile.job.CartExpiration;
import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemTypeEnum;
import com.bibsmobile.model.UserProfile;

public final class CartUtil {
    private static final Logger log = LoggerFactory.getLogger(CartUtil.class);
    public static final String SESSION_ATTR_CART_ID = "cartId";

    private CartUtil() {
        super();
    }

    public static Cart updateOrCreateCart(HttpSession session, Long eventCartItemId, Integer quantity, UserProfile userProfile, String color, String size) {
        // make sure our UserProfile is attached
        userProfile = UserProfile.findUserProfile(userProfile.getId());

        boolean newCart = false;
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
        // create cart if it doesn't exist yet.
        if (cart == null) {
            newCart = true;
            cart = new Cart();
            cart.setStatus(Cart.NEW);
            cart.setCreated(now);
            cart.setUpdated(now);
            cart.setCartItems(new ArrayList<CartItem>());
            // user=null if anonymous
            cart.setUser(user);
            cart.setTimeout(Cart.DEFAULT_TIMEOUT);
            cart.persist();
            session.setAttribute(SESSION_ATTR_CART_ID, cart.getId());
        }

        EventCartItem eventCartItem = EventCartItem.findEventCartItem(eventCartItemId);
        CartItem cartItem;
        // if doesn't have product in cart and not removing
        if (CollectionUtils.isEmpty(cart.getCartItems()) && quantity > 0) {
            cartItem = getCartItem(cart, now, eventCartItem, userProfile, quantity, color, size);
            cartItem.persist();
            cart.getCartItems().add(cartItem);
        } else {
            boolean existedCartItem = false;
            for (CartItem ci : cart.getCartItems()) {
                // if product already exists in cart
                if (ci.getEventCartItem().getId().equals(eventCartItemId)) {

                    existedCartItem = true;
                    // adding
                    if (quantity > 0) {

                        if (quantity != ci.getQuantity()) {
                            if (quantity > ci.getQuantity()) {
                                // only if increasing
                                if (eventCartItem.getAvailable() < quantity) {
                                    quantity = eventCartItem.getAvailable();
                                }
                                eventCartItem.setAvailable(eventCartItem.getAvailable() - quantity);
                            } else {
                                // quantity < ci.getQuantity()
                                // diff>0
                                int diff = ci.getQuantity() - quantity;
                                // add removed quantity to available
                                eventCartItem.setAvailable(eventCartItem.getAvailable() + diff);
                            }
                            eventCartItem.merge();
                            ci.setQuantity(quantity);
                            ci.setUpdated(now);
                            ci.persist();
                        }

                    }
                    // removing
                    else {
                        // add removed quantity to available
                        eventCartItem.setAvailable(eventCartItem.getAvailable() + ci.getQuantity());
                        ci.remove();
                    }
                    eventCartItem.merge();
                    break;
                }
            }

            if (!existedCartItem && quantity > 0) {
                cartItem = getCartItem(cart, now, eventCartItem, userProfile, quantity, color, size);
                cartItem.persist();
                cart.getCartItems().add(cartItem);
            }

        }

        double total = 0;
        for (CartItem ci : cart.getCartItems()) {
            total += (ci.getQuantity() * ci.getPrice());
        }
        cart.setTotal(total);
        cart.merge();

        // schedule expiration of new cart
        if (newCart) {
            try {
                // + 1, because the job tends can fire a little too early
                BaseJob.scheduleOnceInSeconds(CartExpiration.class, cart.getTimeout() + 1);
            } catch (SchedulerException e) {
                log.error("Could not schedule cart expiration.", e);
                return null;
            }
        }

        return cart;
    }

    private static CartItem getCartItem(Cart cart, Date now, EventCartItem eventCartItem, UserProfile userProfile, Integer quantity, String color, String size) {
        CartItem cartItem = new CartItem();
        if (eventCartItem.getType() == EventCartItemTypeEnum.T_SHIRT) {
            cartItem.setColor(color);
            cartItem.setSize(size);
        }
        cartItem.setPrice(eventCartItem.getActualPrice());
        cartItem.setCart(cart);
        cartItem.setEventCartItem(eventCartItem);
        // add maximum available quantity
        if (eventCartItem.getAvailable() < quantity) {
            quantity = eventCartItem.getAvailable();
        }
        cartItem.setQuantity(quantity);
        cartItem.setCreated(now);
        cartItem.setUpdated(now);
        cartItem.setUserProfile(userProfile);

        // updating available quantity in eventcartitem
        eventCartItem.setAvailable(eventCartItem.getAvailable() - quantity);
        eventCartItem.merge();
        return cartItem;
    }

}
