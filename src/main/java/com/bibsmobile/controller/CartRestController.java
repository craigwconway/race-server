package com.bibsmobile.controller;

import com.bibsmobile.model.Cart;
import com.bibsmobile.util.CartUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/rest/carts")
@Controller
public class CartRestController {
    @RequestMapping(value = "/item/{id}/updatequantity", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> updateOrCreateCartJson(@PathVariable("id") Long eventCartItemId, @RequestParam Integer eventCartItemQuantity) {
        Cart cart = CartUtil.updateOrCreateCart(eventCartItemId, eventCartItemQuantity);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (cart == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cart.toJson(), headers, HttpStatus.OK);
    }
}
