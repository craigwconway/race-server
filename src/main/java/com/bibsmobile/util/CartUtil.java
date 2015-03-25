package com.bibsmobile.util;

import com.bibsmobile.job.BaseJob;
import com.bibsmobile.job.CartExpiration;
import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemCoupon;
import com.bibsmobile.model.EventCartItemPriceChange;
import com.bibsmobile.model.EventCartItemTypeEnum;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserProfile;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class CartUtil {
    private static final Logger log = LoggerFactory.getLogger(CartUtil.class);
    public static final String SESSION_ATTR_CART_ID = "cartId";
    private static final double BIBS_ABSOLUTE_FEE = 100;
    private static final double BIBS_RELATIVE_FEE = 0.06;

    private CartUtil() {
        super();
    }

    public static Cart updateOrCreateCart(HttpSession session, Long eventCartItemId, EventCartItemPriceChange priceChange, Integer quantity, UserProfile userProfile, UserGroup team, String color, String size, String couponCode, boolean forceNewItem) {
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
            cartItem = getCartItem(cart, now, priceChange, eventCartItem, userProfile, team, quantity, color, size);
            cartItem.persist();
            cart.getCartItems().add(cartItem);
        } else {
            boolean existedCartItem = false;
            if (!forceNewItem) {
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
                                ci.setUserProfile(userProfile);
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
            }

            if (!existedCartItem && quantity > 0) {
                cartItem = getCartItem(cart, now, priceChange, eventCartItem, userProfile, team, quantity, color, size);
                cartItem.persist();
                cart.getCartItems().add(cartItem);
            }

        }

        // add coupons to cart
        EventCartItemCoupon coupon = null;
        if (couponCode != null) {
            coupon = EventCartItemCoupon.findCouponByCode(cart.getEvent(), couponCode);
        }
        cart.setCoupon(coupon);

        long total = 0;
        // calculate prics of cart without coupons
        for (CartItem ci : cart.getCartItems()) {
            total += (ci.getQuantity() * ci.getPrice() * 100);
        }
        total += total * BIBS_RELATIVE_FEE + BIBS_ABSOLUTE_FEE;
        // calculate discounts of total price based on coupons
        if(cart.getCoupon() != null) {
            total -= cart.getCoupon().getDiscount(total);
        }
        // set total price which is at least 0
        cart.setTotal(Math.max(0, total));
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

    private static CartItem getCartItem(Cart cart, Date now, EventCartItemPriceChange priceChange, EventCartItem eventCartItem, UserProfile userProfile, UserGroup team, Integer quantity, String color, String size) {
        CartItem cartItem = new CartItem();
        if (eventCartItem.getType() == EventCartItemTypeEnum.T_SHIRT) {
            cartItem.setColor(color);
            cartItem.setSize(size);
        }
        
        if (priceChange != null && !priceChange.isValidAt(new Date())) {
        	throw new IllegalArgumentException("Price change not valid right now!");
        }
        
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
        cartItem.setTeam(team);
        cartItem.setEventCartItemPriceChange(priceChange);
        if (priceChange == null) {
        	cartItem.setPrice(eventCartItem.getPrice());
        } else {
        	cartItem.setPrice(priceChange.getPrice());
        }


        // updating available quantity in eventcartitem
        eventCartItem.setAvailable(eventCartItem.getAvailable() - quantity);
        eventCartItem.merge();
        return cartItem;
    }

}
