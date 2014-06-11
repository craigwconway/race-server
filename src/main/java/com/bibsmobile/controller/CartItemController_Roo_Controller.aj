// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.controller;

import com.bibsmobile.controller.CartItemController;
import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.EventCartItem;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect CartItemController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String CartItemController.create(@Valid CartItem cartItem, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, cartItem);
            return "cartitems/create";
        }
        uiModel.asMap().clear();
        cartItem.persist();
        return "redirect:/cartitems/" + encodeUrlPathSegment(cartItem.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String CartItemController.createForm(Model uiModel) {
        populateEditForm(uiModel, new CartItem());
        return "cartitems/create";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String CartItemController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("cartitem", CartItem.findCartItem(id));
        uiModel.addAttribute("itemId", id);
        return "cartitems/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String CartItemController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("cartitems", CartItem.findCartItemEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) CartItem.countCartItems() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("cartitems", CartItem.findAllCartItems(sortFieldName, sortOrder));
        }
        return "cartitems/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String CartItemController.update(@Valid CartItem cartItem, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, cartItem);
            return "cartitems/update";
        }
        uiModel.asMap().clear();
        cartItem.merge();
        return "redirect:/cartitems/" + encodeUrlPathSegment(cartItem.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String CartItemController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, CartItem.findCartItem(id));
        return "cartitems/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String CartItemController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        CartItem cartItem = CartItem.findCartItem(id);
        cartItem.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/cartitems";
    }
    
    void CartItemController.populateEditForm(Model uiModel, CartItem cartItem) {
        uiModel.addAttribute("cartItem", cartItem);
        uiModel.addAttribute("carts", Cart.findAllCarts());
        uiModel.addAttribute("eventcartitems", EventCartItem.findAllEventCartItems());
    }
    
    String CartItemController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}