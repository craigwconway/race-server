package com.bibsmobile.controller;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.CartUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/rest/carts")
@Controller
public class CartRestController {
    @RequestMapping(value = "/item/{id}/updatequantity/{eventCartItemQuantity}", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> updateOrCreateCartJson(@PathVariable("id") Long eventCartItemId, @PathVariable Integer eventCartItemQuantity,
                                                         @RequestBody String userProfileJson, HttpServletRequest request) {
        UserProfile eventCartUserProfile = UserProfile.fromJsonToUserProfile(userProfileJson);
        Cart cart = CartUtil.updateOrCreateCart(request.getSession(), eventCartItemId, eventCartItemQuantity, eventCartUserProfile);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(cart.toJson(), headers, HttpStatus.OK);
    }
}
