package com.bibsmobile.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CustomRegFieldResponse;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.model.wrapper.CartItemReqWrapper;
import com.bibsmobile.service.UserProfileService;
import com.bibsmobile.util.CartUtil;
import com.bibsmobile.util.UserProfileUtil;

@RequestMapping("/rest/carts")
@Controller
public class CartRestController {
	
	private static final Logger log = LoggerFactory.getLogger(EventController.class);
	
    @Autowired
    private UserProfileService userProfileService;

    @RequestMapping(value = "/item/{id}/updatequantity/{eventCartItemQuantity}", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> updateOrCreateCartJson(@PathVariable("id") Long eventCartItemId, @PathVariable Integer eventCartItemQuantity, @RequestBody String json,
            HttpServletRequest request) {
        CartItemReqWrapper cartItemRequestWrapper = CartItemReqWrapper.fromJsonToCartItemReqWrapper(json);
        UserProfile registrationProfile = cartItemRequestWrapper.getUserProfile();
        String color = cartItemRequestWrapper.getColor();
        String size = cartItemRequestWrapper.getSize();
        if (registrationProfile == null) {
            return new ResponseEntity<>("invalid user profile", HttpStatus.BAD_REQUEST);
        }
        UserProfileUtil.disableUserProfile(registrationProfile);
        if (registrationProfile.getId() == null) {
            this.userProfileService.saveUserProfile(registrationProfile);
        }
        Cart cart = CartUtil.updateOrCreateCart(request.getSession(), eventCartItemId, cartItemRequestWrapper.getEventCartItemPriceChange(), eventCartItemQuantity, registrationProfile, cartItemRequestWrapper.getTeam(), color, size, cartItemRequestWrapper.getCouponCode(), cartItemRequestWrapper.isNewItem());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        log.info("Updating cart id: " + cart.getId() + " total: " + cart.getTotal());
        return new ResponseEntity<>(cart.toJsonForCartReturn(), headers, HttpStatus.OK);
    }

    /**
     * @api {post} /rest/carts/checkcoupon/:couponCode Check Coupon
     * @apiName Check Coupon Code
     * @apiGroup restcarts
     * @apiDescription Check a coupon code to see if it is valid. If the coupon is valid, insert it into the cart.
     * If the coupon code is not valid, return the original cart. If no cart is found or a coupon is attempted add before it is found, return
     * error.
     * @apiParam {String} couponCode URL Param of string containing coupon code. The coupon will be added to the cart if it is valid.
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
     * @api {post} /rest/carts/questions
     * @apiGroup restcarts
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
    public ResponseEntity<String> updateOrCreateResponses(@RequestBody Cart cart) {
    	Cart trueCart = Cart.findCart(cart.getId());
    	for(CustomRegFieldResponse crfr : cart.getCustomRegFieldResponses()) {
    		if(crfr.getId() != null) {
    			// check for a match
    			try {
    				CustomRegFieldResponse match = CustomRegFieldResponse.findCustomRegFieldResponse(crfr.getId());
    				match.setResponse(crfr.getResponse());
    				match.merge();
    			} catch(Exception e) {
    				crfr.persist();
    				crfr.setCart(trueCart);
    			}
    		} else {
    			crfr.setCart(trueCart);
    			crfr.persist();
    		}
    	}
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Content-Type", "application/json; charset=utf-8");
    	return new ResponseEntity<>(trueCart.toJson(ArrayUtils.toArray("cartItems", "cartItems.user", "customRegFieldResponses")), headers, HttpStatus.OK);
    }
}
