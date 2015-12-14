package com.bibsmobile.controller;

import java.util.List;
import java.util.Set;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.CustomRegField;
import com.bibsmobile.model.CustomRegFieldResponse;
import com.bibsmobile.model.CustomRegFieldResponseOption;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.model.wrapper.CartItemReqWrapper;
import com.bibsmobile.service.UserProfileService;
import com.bibsmobile.util.CartUtil;
import com.bibsmobile.util.UserProfileUtil;

@RequestMapping("/rest/carts")
@Controller
public class CartRestController {
	
	/**
	 * @apiDefine CartReturn
	 * 
	 * @apiSuccess (200) {Number} id Unique Id of cart
	 * @apiSuccess (200) {Number} total total in cents of cart
	 * @apiSuccess (200) {Number} totalPreFee total in cents of cart before bibs fee is applied
	 * @apiSuccess (200) {Date} created creation datetime of cart
	 * @apiSuccess (200) {Date} last update datetime of cart
	 * @apiSuccess (200) {Number} status Status Code of cart
	 * @apiSuccess (200) {String} referralUrl Url to use for referring other users
	 * @apiSuccess (200) {Boolean} shared Switch to indicate whether the cart has been shared
	 * @apiSuccess (200) {Object} referral cart referring this cart
	 * @apiSuccess (200) {Number} referralDiscount Number in cents discounted by social sharing
	 * @apiSuccess (200) {Number} timeout Timeout of cart
	 * @apiSuccess (200) {Object[]} cartItems Array of cartItem objects in cart
	 * @apiSuccess (200) {Number} cartItems.id Unique ID of Cart Item
	 * @apiSuccess (200) {Object} cartItems.eventCartItem EventCartItem pointed to by CartItem
	 * @apiSuccess (200) {Object[]} customRegFieldResponses Array of responses to custom questions
	 * @apiSuccess (200) {Object} user User controlling cart
	 * @apiSuccess (200) {Number} user.id Id of user controlling cart
	 * @apiSuccess (200) {String} user.firstname firstname of user controlling cart
	 * @apiSuccess (200) {String} user.lastname lastname of user controlling cart
	 * @apiSuccess (200) {String} user.email email of user in cart
	 * @apiSuccess (200) {Object} coupon Coupon in cart
	 * @apiSuccess (200) {Number} coupon.id Id of coupon in cart
	 * @apiSuccess (200) {Number} coupon.discountAbsolute Absolute Discount to apply (if non-null)
	 * @apiSuccess (200) {Number} coupon.discountRelative Relative Discount to apply (if non-null)
	 */
	
	
	private static final Logger log = LoggerFactory.getLogger(EventController.class);
	
    @Autowired
    private UserProfileService userProfileService;

    /**
     * @api {get} /getquestions Get Questions
     * @apiName Get Questions
     * @apiGroup restcarts
     * @apiDescription Get a specific set of customregfields to use in the cart
     * @apiSuccess (200) {Object[]} customRegField
     * @apiSuccess (200) {Number} customRegField.id Id of question
     * @apiSuccess (200) {String} customRegField.question Text of question
     * @apiSuccess (200) {String} customRegField.responseSet Set of comma-delimited responses to use in dropdown. If blank, display a text field.
     */
    @RequestMapping(value = "/getquestions", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> getQuestions(HttpServletRequest request) {
    	Cart c = CartUtil.getCartFromSession(request.getSession());
    	System.out.println("Cart in session: " + c);
	    List<CustomRegField> possibleFields = CustomRegField.findVisibleCustomRegFieldsByEvent(c.getEvent()).getResultList();
	    List<CustomRegField> returnFields = new LinkedList<CustomRegField>();
	    Set<Long> cartItemIds = new HashSet<Long>();
	    for(CartItem ci : c.getCartItems()) {
	    	cartItemIds.add(ci.getEventCartItem().getId());
	    }
	    for(CustomRegField field : possibleFields) {
	    	if(!field.isAllItems()) {
	    		for(Long id : field.getEventItemIds()) {
	    			if(cartItemIds.contains(id)) {
	    				returnFields.add(field);
	    				break;
	    			}
	    		}
	    	} else {
	    		returnFields.add(field);
	    	}
	    }
	    System.out.println("Possible Fields: " + possibleFields.size());
	    System.out.println("Returned Fields: " + returnFields.size());
	    HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
	    return new ResponseEntity<>(CustomRegField.toJsonArray(returnFields), headers, HttpStatus.OK);
    }
    
    
    @RequestMapping(value = "/unansweredquestions/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> getQuestionsForId(HttpServletRequest request,
    		@PathVariable("id") Long cartId) {
    	Cart c = Cart.findCart(cartId);
	    List<CustomRegField> possibleFields = CustomRegField.findVisibleCustomRegFieldsByEvent(c.getEvent()).getResultList();
	    List<CustomRegField> returnFields = new LinkedList<CustomRegField>();
	    Set<Long> cartItemIds = new HashSet<Long>();
	    for(CartItem ci : c.getCartItems()) {
	    	cartItemIds.add(ci.getEventCartItem().getId());
	    }
	    for(CustomRegField field : possibleFields) {
	    	if(!field.isAllItems()) {
	    		for(Long id : field.getEventItemIds()) {
	    			if(cartItemIds.contains(id)) {
	    				returnFields.add(field);
	    				break;
	    			}
	    		}
	    	} else {
	    		returnFields.add(field);
	    	}
	    }
	    for(CustomRegFieldResponse response : c.getCustomRegFieldResponses()) {
	    	returnFields.remove(response.getCustomRegField());
	    }
	    System.out.println("Possible Fields: " + possibleFields.size());
	    System.out.println("Returned Fields: " + returnFields.size());
	    HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
	    return new ResponseEntity<>(CustomRegField.toJsonArray(returnFields), headers, HttpStatus.OK);
    }
    
    
    
    /**
     * @api {post} /item/:id/updatequantity/:eventCartItemQuantity Add/Update Cart Item
     * @apiName Add/Update Cart Item
     * @apiGroup restcarts
     * @apiDescription Add or update a quantity of an eventcartitem in a cart. If no cart has been created previously, this call creates a new cart.
     * If no user is linked with the cart, this call adds a new user profile. Setting quantity to 0 removes from cart, adding a duplicate
     * item of a different size/color needs the newItem switch.
     * @apiParam {Object} [userProfile] user object linked with cart request
     * @apiParam {Number} [userProfile.id] unique ID of user profile
     * @apiParam {Object} [userGroup] team the cart belongs to
     * @apiParam {Number} [userGroup.id] unique ID of team
     * @apiParam {Boolean} [newItem=false] Switch to add a new item of this type instead of modifying previous items of this type in cart
     * @apiParam {String} [size] size of SHIRT type cart items
     * @apiParam {String} [color] color of SHIRT type cart items
     * @apiParam {Number} [priceChangeId] ID of pricechange to use with this object
     * @apiParam {Number} [referral] ID of referring cart
     * @apiParam {Number} [deleteId] ID of specific Cart Item to delete
     * @apiSampleRequest http://localhost:8080/bibs-server/item/:id/updatequantity/:eventCartItemQuantity
     * @apiUse CartReturn
     * @return
     */
    @RequestMapping(value = "/item/{id}/updatequantity/{eventCartItemQuantity}", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> updateOrCreateCartJson(@PathVariable("id") Long eventCartItemId, @PathVariable Integer eventCartItemQuantity, @RequestBody String json,
            HttpServletRequest request) {
        CartItemReqWrapper cartItemRequestWrapper = CartItemReqWrapper.fromJsonToCartItemReqWrapper(json);
        UserProfile registrationProfile = cartItemRequestWrapper.getUserProfile();
        String color = cartItemRequestWrapper.getColor();
        String size = cartItemRequestWrapper.getSize();
        Long referral = cartItemRequestWrapper.getReferral();
        if (registrationProfile == null) {
            return new ResponseEntity<>("invalid user profile", HttpStatus.BAD_REQUEST);
        }
        UserProfileUtil.disableUserProfile(registrationProfile);
        if (registrationProfile.getId() == null) {
            this.userProfileService.saveUserProfile(registrationProfile);
        }
        Cart cart = CartUtil.updateOrCreateCart(request.getSession(), eventCartItemId, cartItemRequestWrapper.getEventCartItemPriceChange(), eventCartItemQuantity, registrationProfile, cartItemRequestWrapper.getTeam(), color, size, cartItemRequestWrapper.getCouponCode(), cartItemRequestWrapper.isNewItem(), referral, cartItemRequestWrapper.getDeleteId());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        log.info("Updating cart id: " + cart.getId() + " total: " + cart.getTotal());
        return new ResponseEntity<>(cart.toJsonForCartReturn(), headers, HttpStatus.OK);
    }
    
    /**
     * @api {post} /rest/carts/socialshare Social Share
     * @apiName Social Share
     * @apiGroup restcarts
     * @apiDescription Mark a cart social shared for modifying the totals calculations. Post to this url with an empty body, response
     * codes denote how share is processed. 202 = discount applied, 200 = shared only.
     * @apiSampleRequest http://localhost:8080/bibs-server/rest/carts/socialshare
     * @ApiUse CartReturn
     * 
     * @return
     */
    @RequestMapping(value = "/socialshare", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> socialShare(HttpServletRequest request) {
        Cart cart = CartUtil.markSocialShared(request.getSession());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        log.info("Updating cart id: " + cart.getId() + " total: " + cart.getTotal());
        if(cart.getReferralDiscount() > 0 ) {
        	return new ResponseEntity<>(cart.toJsonForCartReturn(), headers, HttpStatus.ACCEPTED);
        } else {
        	return new ResponseEntity<>(cart.toJsonForCartReturn(), headers, HttpStatus.OK);
        }
        
    }
    
    /**
     * @api {post} /rest/carts/checkcoupon/:couponCode Check Coupon
     * @apiName Check Coupon Code
     * @apiGroup restcarts
     * @apiDescription Check a coupon code to see if it is valid. If the coupon is valid, insert it into the cart.
     * If the coupon code is not valid, return the original cart. If no cart is found or a coupon is attempted add before it is found, return
     * error.
     * @apiParam {String} couponCode URL Param of string containing coupon code. The coupon will be added to the cart if it is valid.
     * @apiUse CartReturn
     * @param couponCode
     * @param request
     * @return
     */
    @RequestMapping(value = "/checkcoupon/{couponCode}", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> checkCoupon(@PathVariable("couponCode") String couponCode,
            HttpServletRequest request) {
    	couponCode = StringUtils.upperCase(couponCode);
    	log.info("Checking Coupon with code: " + couponCode);
        Cart cart = CartUtil.checkCoupon(request.getSession(), couponCode);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if(cart.getCoupon() != null && cart.getCoupon().getCode().equals(couponCode)) {
        	log.info("Attempt to add coupon " + couponCode + " to cart ID " + cart.getId() + " successful");
            return new ResponseEntity<>(cart.toJson(ArrayUtils.toArray("cartItems", "cartItems.user")), headers, HttpStatus.OK);	
        } else {
        	log.info("Attempt to add coupon " + couponCode + " to cart ID " + cart.getId() + " failed");
        	return new ResponseEntity<>(cart.toJsonForCartReturn(), headers, HttpStatus.NOT_ACCEPTABLE);
        }
    }    
    
    /**
     * @api {post} /rest/carts/questions Post Questions
     * @apiGroup restcarts Post Questions
     * @apiName postQuestions
     * @apiParam {Object} cart Cart object containing questions
     * @apiParam {Number} cart.id Id of posted cart
     * @apiParam {Object[]} cart.customRegFieldResponses An array of custom reg field responses selected by the user.
     * @apiParam {Number} [cart.customRegFieldResponses.id] Id of customregfieldresponse to post. Include this to update an answer
     * @apiParam {String} [cart.customRegFieldResponses.response] String containing the response to the question
     * @apiParam {Object} cart.customRegFieldResponses.customRegField Regfield answered. Must contain id.
     * @apiParam {Number} cart.customRegFieldResponses.customRegField.id id of linked CustomRegField
     * @apiParamExample {json} Sample Create
     * 		{
     * 			"id": 1,
     * 			"customRegFieldResponses": 
     * 				[
     * 					{
     * 						"customRegField": {"id":2},
     * 						"response": "nodejs"
     * 					}
     * 				]
     * 		}
     * @apiParamExample {json} Sample Update
     * 		{
     * 			"id": 1,
     * 			"customRegFieldResponses": 
     * 				[
     * 					{
     * 						"id": 1,
     * 						"customRegField": {"id":2},
     * 						"response": "nodejs"
     * 					}
     * 				]
     * 		}
     * @apiSuccess (200) {Object} cart Modified cart object
     */
    @RequestMapping(value = "/questions", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> updateOrCreateResponses(@RequestBody Cart cart, HttpServletRequest request) {
    	Cart trueCart = Cart.findCart(cart.getId());
    	/*for(CustomRegFieldResponse crfr : trueCart.getCustomRegFieldResponses()) {
    		crfr.merge().remove();
    		crfr.flush();
    		System.out.println("deleting custom field");
    	}*/
    	/*
    	Iterator <CustomRegFieldResponse> oldResponses = trueCart.getCustomRegFieldResponses().iterator();
    	trueCart.setCustomRegFieldResponses(null);
    	trueCart.merge();
    	while(oldResponses.hasNext()) {
    		CustomRegFieldResponse toRemove = oldResponses.next();
    		toRemove.remove();
    	}
    	
    	for(CustomRegFieldResponse crfr : cart.getCustomRegFieldResponses()) {
    		CustomRegField upField = crfr.getCustomRegField();
    		CustomRegField field = upField != null ? CustomRegField.findCustomRegField(upField.getId()) : null;
    		if (field != null) {
    			Set <CustomRegFieldResponseOption> options = field.getResponseSet();
    			crfr.setCart(trueCart);
				CustomRegFieldResponseOption uploadedOption = new CustomRegFieldResponseOption();
				uploadedOption.setResponse(crfr.getResponse());
				System.out.println("Reg Field Options: " + options);
				if(!options.isEmpty() && options.contains(uploadedOption)) {
					List <CustomRegFieldResponseOption> optionsList = new ArrayList<CustomRegFieldResponseOption>(options);
					for(CustomRegFieldResponseOption realOption : optionsList) {
						if(realOption.equals(uploadedOption)) {
							crfr.setPrice(realOption.getPrice());
						}
					}
				}
    			crfr.persist();
			
    		}
    	}
    	Cart realCart = null;
    	try{
    		realCart = CartUtil.processQuestions(request.getSession());
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	System.out.println("realcart: " + realCart);

    	for(CustomRegFieldResponse crfr : realCart.getCustomRegFieldResponses()) {
    		Hibernate.initialize(crfr.getResponse());
    	}
    	*/
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Content-Type", "application/json; charset=utf-8");
    	return new ResponseEntity<>(CartUtil.processQuestions(request.getSession(), cart).toJson(ArrayUtils.toArray("cartItems", "cartItems.user", "customRegFieldResponses")), headers, HttpStatus.OK);
    }
}
