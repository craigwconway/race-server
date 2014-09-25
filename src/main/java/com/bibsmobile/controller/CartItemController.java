package com.bibsmobile.controller;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.UserProfile;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/cartitems")
@Controller
public class CartItemController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid CartItem cartItem, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, cartItem);
            return "cartitems/create";
        }
        uiModel.asMap().clear();
        cartItem.persist();
        return "redirect:/cartitems/" + encodeUrlPathSegment(cartItem.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new CartItem());
        return "cartitems/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("cartitem", CartItem.findCartItem(id));
        uiModel.addAttribute("itemId", id);
        return "cartitems/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
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
    public String update(@Valid CartItem cartItem, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, cartItem);
            return "cartitems/update";
        }
        uiModel.asMap().clear();
        cartItem.merge();
        return "redirect:/cartitems/" + encodeUrlPathSegment(cartItem.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, CartItem.findCartItem(id));
        return "cartitems/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        CartItem cartItem = CartItem.findCartItem(id);
        cartItem.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/cartitems";
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public static void export(
            @RequestParam Long eventId, @RequestParam boolean all, HttpServletResponse response) throws IOException {
        List<CartItem> cartItems = new ArrayList<>();
        Event event = Event.findEvent(eventId);
        if (event != null) {
            List<EventCartItem> eventCartItems = EventCartItem.findEventCartItemsByEvent(event).getResultList();
            if (!eventCartItems.isEmpty()) {
                cartItems = CartItem.findCompletedCartItemsByEventCartItems(eventCartItems, all).getResultList();
            }
        }
        response.setContentType("text/csv;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + "registrations.csv\"");
        for (CartItem cartItem : cartItems) {
            if (BooleanUtils.isNotTrue(cartItem.getExported())) {
                cartItem.setExported(Boolean.TRUE);
                cartItem.persist();
            }
            EventCartItem eventCartItem = cartItem.getEventCartItem();
            UserProfile userProfile = cartItem.getUserProfile();
            String str = cartItem.getCreated() + ", "
                    + cartItem.getQuantity() + ", "
                    + cartItem.getCoupon() + ", "
                    + cartItem.getPrice() + ", "
                    + cartItem.getSize() + ", "
                    + cartItem.getColor() + ", "
                    + eventCartItem.getType() + ", "
                    + eventCartItem.getName() + ", "
                    + eventCartItem.getPrice() + ", ";

            if (userProfile != null) {
                str += userProfile.getBirthdate() + ", "
                        + userProfile.getEmail() + ", "
                        + userProfile.getPhone() + ", "
                        + userProfile.getAddressLine1() + ", "
                        + userProfile.getAddressLine2() + ", "
                        + userProfile.getZipCode() + ", "
                        + userProfile.getEmergencyContactName() + ", "
                        + userProfile.getEmergencyContactPhone() + ", "
                        + userProfile.getHearFrom();
            }
            str += "\r\n";
            response.getWriter().write(str);

        }
        response.getWriter().flush();
        response.getWriter().close();
    }

    void populateEditForm(Model uiModel, CartItem cartItem) {
        uiModel.addAttribute("cartItem", cartItem);
        uiModel.addAttribute("carts", Cart.findAllCarts());
        uiModel.addAttribute("eventcartitems", EventCartItem.findAllEventCartItems());
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }

}
