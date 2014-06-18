package com.bibsmobile.controller;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.CartUtil;
import com.bibsmobile.util.UserProfileUtil;
import net.authorize.sim.Fingerprint;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RequestMapping("/carts")
@Controller
@RooWebScaffold(path = "carts", formBackingObject = Cart.class)
@RooWebJson(jsonObject = Cart.class)
public class CartController {

    @RequestMapping(value = "/item/{id}", produces = "text/html")
    public String view(@PathVariable("id") Long eventCartItemId) {
        return "cart";
    }

    @RequestMapping(value = "/item/{id}/updatequantity", produces = "text/html")
    public String updateItemQuantity(@PathVariable("id") Long eventCartItemId, @RequestParam Integer quantity, Model uiModel,
                                     @ModelAttribute UserProfile userProfile, HttpServletRequest request) {
        if (userProfile.getId() == null) {
            UserProfileUtil.disableUserProfile(userProfile);
            userProfileService.saveUserProfile(userProfile);
        }
        //TODO: implement if needed color and size in admin panel
        Cart cart = CartUtil.updateOrCreateCart(request.getSession(), eventCartItemId, quantity, userProfile, null, null);
        uiModel.addAttribute("cart", cart);
        return "redirect:/carts/item/" + eventCartItemId;
    }

}