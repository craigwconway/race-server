package com.bibsmobile.util;

import com.bibsmobile.job.BaseJob;
import com.bibsmobile.job.CartExpiration;
import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.CustomRegFieldResponse;
import com.bibsmobile.model.CustomRegFieldResponseOption;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemCoupon;
import com.bibsmobile.model.EventCartItemPriceChange;
import com.bibsmobile.model.EventCartItemTypeEnum;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.model.CustomRegField;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class CartUtil {
    private static final Logger log = LoggerFactory.getLogger(CartUtil.class);
    public static final String SESSION_ATTR_CART_ID = "cartId";
    private static final double BIBS_ABSOLUTE_FEE = 100;
    private static final double BIBS_RELATIVE_FEE = 0.06;
    public static final long BIBS_SOCIAL_DISCOUNT = 100;

    private CartUtil() {
        super();
    }
    
    public static Cart getCartFromSession(HttpSession session) {
    	UserProfile user = null;
    	Cart cart = null;
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
	            log.error("Caught exception finding cart in session");
	        }
	    }
	    return cart;
    }
    
    public static Cart checkCoupon(HttpSession session, String couponCode) {
        Long cartIdFromSession = (Long) session.getAttribute(SESSION_ATTR_CART_ID);
        Cart cart = null;
        UserProfile user = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            if (!username.equals("anonymousUser")) {
                user = UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
            }
        }
        
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
                log.error("Caught exception finding cart in session");
                return null;
            }
        }
        if (cart == null) {
        	log.info("Null cart found when adding coupon");
        	return null;
        }
        // add coupons to cart
        EventCartItemCoupon coupon = null;
        if (couponCode != null) {
            coupon = EventCartItemCoupon.findCouponByCode(cart.getEvent(), couponCode);
        }
        if(coupon == null || coupon.getAvailable() <= 0) {
        	return cart;
        }
        coupon.setAvailable(coupon.getAvailable() - 1);
        coupon.setUsed(coupon.getUsed() + 1);
        coupon.merge();
        cart.setCoupon(coupon);

        long total = 0;
        // First, add up price of all cart items without donations:
        for (CartItem ci : cart.getCartItems()) {
        	if(ci.getEventCartItem().getType() != EventCartItemTypeEnum.DONATION) {
                total += (ci.getQuantity() * ci.getPrice() * 100);
        	}
        }
        if(cart.isShared()) {
            if(cart.getEvent().isSocialSharingDiscounts()) {
    	        if(total > cart.getEvent().getSocialSharingDiscountAmount()) {
    	        	total -= cart.getEvent().getSocialSharingDiscountAmount();
    	        	cart.setReferralDiscount(cart.getEvent().getSocialSharingDiscountAmount());
    	        }
    	        else if(total > 0) {
    	        	cart.setReferralDiscount(total);
    	        	cart.setTotal(0);
    	        }
            }
        }
        if(cart.getQuestions() != 0) {
        	total += cart.getQuestions();
        }
        // Then apply coupon:
        if(cart.getCoupon() != null) {
        	log.info("Adding coupon to cart id: " + cart.getId() + " with precoupon total " + total);
        	total -= cart.getCoupon().getDiscount(total);
        	log.info("Adding coupon to cart id: " + cart.getId() + " with postcoupon total " + total);
        }
        // Then add in donations:
        for (CartItem ci : cart.getCartItems()) {
        	if(ci.getEventCartItem().getType() == EventCartItemTypeEnum.DONATION) {
                total += (ci.getQuantity() * ci.getPrice() * 100);
        	}
        }
        cart.setTotalPreFee(total);
        // Finally, apply bibs processing fee. TODO: put this at level of event.
        double absolute;
        double relative;
        try {
            absolute = cart.getEvent().getPricing().getAbsoluteFee();
            relative = cart.getEvent().getPricing().getRelative();
        } catch (Exception e) {
        	absolute = BIBS_ABSOLUTE_FEE;
        	relative = BIBS_RELATIVE_FEE;
        }
        if(total > 0) {
        	total += total * relative + absolute;
        }
        // set total price which is at least 0
        cart.setTotal(Math.max(0, total));
        cart.merge();
        return cart;
    }

    
    public static Cart processQuestions(HttpSession session, Cart incoming) {
        Long cartIdFromSession = (Long) session.getAttribute(SESSION_ATTR_CART_ID);
        Cart cart = null;
        UserProfile user = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            if (!username.equals("anonymousUser")) {
                user = UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
            }
        }
        
        if (cartIdFromSession != null) {
            cart = Cart.findCart(cartIdFromSession);
        } else if (user != null) {
            try {
                List<Cart> carts = Cart.findCartsByUser(user).getResultList();
                for (Cart c : carts) {
                    if (c.getStatus() == Cart.NEW) {
                        cart = c;
                        //session.setAttribute(SESSION_ATTR_CART_ID, cart.getId());
                    }
                }
            } catch (Exception e) {
            	System.out.println("Caught exception finding cart in session");
                log.error("Caught exception finding cart in session");
                return null;
            }
        }
        if (cart == null) {
        	System.out.println("Null cart found with id");
        	log.info("Null cart found modifying questions");
        	return null;
        }
        
    	Iterator <CustomRegFieldResponse> oldResponses = cart.getCustomRegFieldResponses().iterator();
    	cart.setCustomRegFieldResponses(null);
    	cart.merge();
    	while(oldResponses.hasNext()) {
    		CustomRegFieldResponse toRemove = oldResponses.next();
    		toRemove.remove();
    	}
    	List <CustomRegFieldResponse> responses = new ArrayList <CustomRegFieldResponse>();
    	for(CustomRegFieldResponse crfr : incoming.getCustomRegFieldResponses()) {
    		CustomRegField upField = crfr.getCustomRegField();
    		CustomRegField field = upField != null ? CustomRegField.findCustomRegField(upField.getId()) : null;
    		if (field != null) {
    			Set <CustomRegFieldResponseOption> options = field.getResponseSet();
    			CustomRegFieldResponse toAdd = new CustomRegFieldResponse();
    			toAdd.setCart(cart);
    			toAdd.setCustomRegField(field);
    			toAdd.setResponse(crfr.getResponse());
				CustomRegFieldResponseOption uploadedOption = new CustomRegFieldResponseOption();
				uploadedOption.setResponse(crfr.getResponse());
				System.out.println("Reg Field Options: " + options);
				if(options != null && options.contains(uploadedOption)) {
					List <CustomRegFieldResponseOption> optionsList = new ArrayList<CustomRegFieldResponseOption>(options);
					for(CustomRegFieldResponseOption realOption : optionsList) {
						if(realOption.equals(uploadedOption)) {
							toAdd.setPrice(realOption.getPrice());
						}
					}
				}
    			toAdd.persist();
    			responses.add(toAdd);
    		}
    	}
        cart.setCustomRegFieldResponses(responses);
        cart.merge();
        
        
        
        
        long total = 0;
        // First, add up price of all cart items without donations:
        for (CartItem ci : cart.getCartItems()) {
        	if(ci.getEventCartItem().getType() != EventCartItemTypeEnum.DONATION) {
                total += (ci.getQuantity() * ci.getPrice() * 100);
        	}
        }
        if(cart.getEvent().isSocialSharingDiscounts() && cart.isShared()) {
	        if(total > cart.getEvent().getSocialSharingDiscountAmount()) {
	        	total -= cart.getEvent().getSocialSharingDiscountAmount();
	        	cart.setReferralDiscount(cart.getEvent().getSocialSharingDiscountAmount());
	        }
	        else if(total > 0) {
	        	cart.setReferralDiscount(total);
	        	cart.setTotal(0);
	        }
        }
        long questiontotal=0;
        for(CustomRegFieldResponse response : cart.getCustomRegFieldResponses()) {
        	if(response.getPrice() != null) {
        		questiontotal += response.getPrice();
        	}
        }
        if((-1 * questiontotal) > total){
        	questiontotal = -1*total;
        }
        cart.setQuestions(questiontotal);
        total += questiontotal;
        // Then apply coupon:
        if(cart.getCoupon() != null) {
        	log.info("Adding coupon to cart id: " + cart.getId() + " with precoupon total " + total);
        	total -= cart.getCoupon().getDiscount(total);
        	log.info("Adding coupon to cart id: " + cart.getId() + " with postcoupon total " + total);
        }
        // Then add in donations:
        for (CartItem ci : cart.getCartItems()) {
        	if(ci.getEventCartItem().getType() == EventCartItemTypeEnum.DONATION) {
                total += (ci.getQuantity() * ci.getPrice() * 100);
        	}
        }
        cart.setTotalPreFee(total);
        // Finally, apply bibs processing fee. TODO: put this at level of event.
        double absolute;
        double relative;
        try {
            absolute = cart.getEvent().getPricing().getAbsoluteFee();
            relative = cart.getEvent().getPricing().getRelative();
        } catch (Exception e) {
        	absolute = BIBS_ABSOLUTE_FEE;
        	relative = BIBS_RELATIVE_FEE;
        }
        if(total > 0) {
        	total += total * relative + absolute;
        }
        // set total price which is at least 0
        cart.setTotal(Math.max(0, total));
        cart.merge();
        return cart;
    }    
    
    
    public static Cart markSocialShared(HttpSession session) {
        Long cartIdFromSession = (Long) session.getAttribute(SESSION_ATTR_CART_ID);
        Cart cart = null;
        UserProfile user = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            if (!username.equals("anonymousUser")) {
                user = UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
            }
        }
        
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
                log.error("Caught exception finding cart in session");
                return null;
            }
        }
        if (cart == null) {
        	log.info("Null cart found when marking social");
        	return null;
        }
        cart.setShared(true);
        long total = 0;
        // First, add up price of all cart items without donations:
        for (CartItem ci : cart.getCartItems()) {
        	if(ci.getEventCartItem().getType() != EventCartItemTypeEnum.DONATION) {
                total += (ci.getQuantity() * ci.getPrice() * 100);
        	}
        }
        if(cart.getEvent().isSocialSharingDiscounts()) {
	        if(total > cart.getEvent().getSocialSharingDiscountAmount()) {
	        	total -= cart.getEvent().getSocialSharingDiscountAmount();
	        	cart.setReferralDiscount(cart.getEvent().getSocialSharingDiscountAmount());
	        }
	        else if(total > 0) {
	        	cart.setReferralDiscount(total);
	        	cart.setTotal(0);
	        }
        }
        // Then apply coupon:
        if(cart.getCoupon() != null) {
        	log.info("Adding coupon to cart id: " + cart.getId() + " with precoupon total " + total);
        	total -= cart.getCoupon().getDiscount(total);
        	log.info("Adding coupon to cart id: " + cart.getId() + " with postcoupon total " + total);
        }
        // Then add in donations:
        for (CartItem ci : cart.getCartItems()) {
        	if(ci.getEventCartItem().getType() == EventCartItemTypeEnum.DONATION) {
                total += (ci.getQuantity() * ci.getPrice() * 100);
        	}
        }
        cart.setTotalPreFee(total);
        // Finally, apply bibs processing fee. TODO: put this at level of event.
        double absolute;
        double relative;
        try {
            absolute = cart.getEvent().getPricing().getAbsoluteFee();
            relative = cart.getEvent().getPricing().getRelative();
        } catch (Exception e) {
        	absolute = BIBS_ABSOLUTE_FEE;
        	relative = BIBS_RELATIVE_FEE;
        }
        if(total > 0) {
        	total += total * relative + absolute;
        }
        // set total price which is at least 0
        cart.setTotal(Math.max(0, total));
        cart.merge();
        return cart;
    }    
    
    public static Cart updateOrCreateCart(HttpSession session, Long eventCartItemId, EventCartItemPriceChange priceChange, Integer quantity, UserProfile userProfile, UserGroup team, String color, String size, String couponCode, boolean forceNewItem, Long referral, Long deleteId) {
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
                log.error("Caught exception finding cart in session");
            }
        }
        
        EventCartItem eventCartItem = EventCartItem.findEventCartItem(eventCartItemId);
        Date now = new Date();
        // create cart if it doesn't exist yet.
        if (cart == null) {
            newCart = true;
            cart = new Cart();
            if(referral != null) {
            	Cart referralCart = Cart.findCart(referral);
            	cart.setReferral(referralCart);
            }
            cart.setStatus(Cart.NEW);
            cart.setCreated(now);
            cart.setUpdated(now);
            cart.setCartItems(new ArrayList<CartItem>());
            // user=null if anonymous
            cart.setUser(user);
            cart.setTimeout(Cart.DEFAULT_TIMEOUT);
            cart.persist();
            session.setAttribute(SESSION_ATTR_CART_ID, cart.getId());
            cart.setReferralUrl(BuildTypeUtil.getBuild().getFrontend() + "/registration/#/" + eventCartItem.getEvent().getId() + "?ref=" + cart.getId());
            cart.merge();
        }

    	CartItem cartItemToRemove = null;

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
                        else if(deleteId != null && ci.getId() == deleteId){
                            // add removed quantity to available
                        	System.out.println("Removing " + ci.getQuantity() +" of " + eventCartItem.getName() + "...");
                            eventCartItem.setAvailable(eventCartItem.getAvailable() + ci.getQuantity());
                            cartItemToRemove = ci;
                        } else if(deleteId == null) {
                        	System.out.println("Removing " + ci.getQuantity() +" of " + eventCartItem.getName() + "...");
                            eventCartItem.setAvailable(eventCartItem.getAvailable() + ci.getQuantity());
                            cartItemToRemove = ci;
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
        System.out.println("Size of cart: " + cart.getCartItems().size());
        if(cartItemToRemove != null) {
            cart.getCartItems().remove(cartItemToRemove);
            cart.merge();
        }
        System.out.println("Size of cart: " + cart.getCartItems().size());


        // add coupons to cart
        EventCartItemCoupon coupon = null;
        if (couponCode != null) {
            coupon = EventCartItemCoupon.findCouponByCode(cart.getEvent(), couponCode);
        }
        cart.setCoupon(coupon);

        long total = 0;
        
        // First, add up price of all cart items without donations:
        for (CartItem ci : cart.getCartItems()) {
        	if(ci.getEventCartItem().getType() != EventCartItemTypeEnum.DONATION) {
                total += (ci.getQuantity() * ci.getPrice() * 100);
        	}
        }
        
        if(cart.isShared()) {
            if(cart.getEvent().isSocialSharingDiscounts()) {
    	        if(total > cart.getEvent().getSocialSharingDiscountAmount()) {
    	        	total -= cart.getEvent().getSocialSharingDiscountAmount();
    	        	cart.setReferralDiscount(cart.getEvent().getSocialSharingDiscountAmount());
    	        }
    	        else if(total > 0) {
    	        	cart.setReferralDiscount(total);
    	        	cart.setTotal(0);
    	        }
            }
        }
        if(cart.getQuestions() != 0) {
        	total += cart.getQuestions();
        }
        
        // Then apply coupon:
        if(cart.getCoupon() != null) {
        	log.info("Adding coupon to cart id: " + cart.getId() + " with precoupon total " + total);
            total -= cart.getCoupon().getDiscount(total);
            log.info("Adding coupon to cart id: " + cart.getId() + " with postcoupon total " + total);
        }
        // Then add in donations:
        for (CartItem ci : cart.getCartItems()) {
        	if(ci.getEventCartItem().getType() == EventCartItemTypeEnum.DONATION) {
                total += (ci.getQuantity() * ci.getPrice() * 100);
        	}
        }
        // Finally, apply bibs processing fee. TODO: put this at level of event.
        cart.setTotalPreFee(total);
        double absolute;
        double relative;
        try {
            absolute = cart.getEvent().getPricing().getAbsoluteFee();
            relative = cart.getEvent().getPricing().getRelative();
        } catch (Exception e) {
        	absolute = BIBS_ABSOLUTE_FEE;
        	relative = BIBS_RELATIVE_FEE;
        }
        if(total > 0) {
        	total += total * relative + absolute;
        }
        
        // calculate discounts of total price based on coupons

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
        cartItem.setEventType(eventCartItem.getEventType());
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
