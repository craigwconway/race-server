package com.bibsmobile.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
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
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.model.wrapper.CartItemReqWrapper;
import com.bibsmobile.service.UserProfileService;
import com.bibsmobile.util.CartUtil;
import com.bibsmobile.util.UserProfileUtil;

@RequestMapping("/rest/carts")
@Controller
public class CartRestController {
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
        Cart cart = CartUtil.updateOrCreateCart(request.getSession(), eventCartItemId, cartItemRequestWrapper.getEventCartItemPriceChange(), eventCartItemQuantity, registrationProfile, color, size);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(cart.toJson(ArrayUtils.toArray("cartItems", "cartItems.user")), headers, HttpStatus.OK);
    }
}
