package com.bibsmobile.controller;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.CustomRegField;
import com.bibsmobile.model.CustomRegFieldResponse;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemTypeEnum;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@RequestMapping("/cartitems")
@Controller
public class CartItemController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid CartItem cartItem, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, cartItem);
            return "cartitems/create";
        }
        uiModel.asMap().clear();
        cartItem.persist();
        return "redirect:/cartitems/" + this.encodeUrlPathSegment(cartItem.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        this.populateEditForm(uiModel, new CartItem());
        return "cartitems/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("cartitem", CartItem.findCartItem(id));
        uiModel.addAttribute("itemId", id);
        return "cartitems/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
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
            this.populateEditForm(uiModel, cartItem);
            return "cartitems/update";
        }
        uiModel.asMap().clear();
        cartItem.merge();
        return "redirect:/cartitems/" + this.encodeUrlPathSegment(cartItem.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        this.populateEditForm(uiModel, CartItem.findCartItem(id));
        return "cartitems/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        CartItem cartItem = CartItem.findCartItem(id);
        cartItem.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/cartitems";
    }

    /**
     * @api {get} /cartitems/export Export Registrations
     * @apiName Export Registrations
     * @apiDescription Call to export a CSV of registrations 
     * @apiGroup registrations
     * @apiParam {Number} eventId Id of event to export registrations from as querystring
     * @apiParam {Boolean} all Switch to export all or only new registrations of this type as querystring
     * @apiParam {String="TICKET","T_SHIRT","DONATION"} [type] Type of EventCartItems to export as querystring
     * @apiParam {Boolean} [questions=true] Switch to export custom Fields as querystring
     * @apiParam {Boolean} [bibnum=false] Switch to export assigned bib numbers as querystring
     * @throws IOException
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public static void export(@RequestParam Long eventId, @RequestParam boolean all, HttpServletResponse response,
    		@RequestParam(value = "type") List <EventCartItemTypeEnum> types,
    		@RequestParam(value = "questions", defaultValue = "true") boolean questions,
    		@RequestParam(value = "bibnum", defaultValue = "false") boolean bibnum,
    		@RequestParam(value = "quick", defaultValue = "false") boolean quick) throws IOException {
        List<CartItem> cartItems = new ArrayList<>();
        if(quick) {
        	types = new ArrayList<EventCartItemTypeEnum> ();
        	types.add(EventCartItemTypeEnum.TICKET);
        }
        System.out.println("Handling export for types: " + types);
        Event event = Event.findEvent(eventId);
        if (event != null) {
            List<EventCartItem> eventCartItems = EventCartItem.findEventCartItemsByEventAndTypes(event, types).getResultList();
            System.out.println(eventCartItems.size() + " ECIs matching criteria, triggering export");
            if (!eventCartItems.isEmpty()) {
                cartItems = CartItem.findCompletedCartItemsByEventCartItems(eventCartItems, all).getResultList();
            }
        }
        System.out.println(cartItems.size() + " CIs matching criteria, triggering export");
        //Format all time strings in the event director's timezone:
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        format.setTimeZone(event.getTimezone());
        
        response.setContentType("text/csv;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "registrations.csv\"");
        String headers = "Checkout Time, Ticket Type, Item, Quantity, Size, Color, Coupon Code, Raw Price, Price Option, Social Shared, ";
        if(quick) {
        	headers = "";
        }
        headers += "Event Type,";
        if(bibnum == true) {
        	headers += " Bib Number,";
        }
        List<CustomRegField> fields = CustomRegField.findCustomRegFieldsByEvent(event).getResultList();
        headers += "Firstname, Lastname, Age, Gender, Email, Phone, Address, Zip Code, City, State, Emergency Contact, Emergency Contact Phone";
        if(questions) {
        	for(CustomRegField field : fields ) {
        		headers += ", " + field.getQuestion();
        	}
        }
        headers += "\r\n";
        response.getWriter().write(headers);
        for (CartItem cartItem : cartItems) {
        	System.out.println("Checking cartitem: " + cartItem.getId());
            if (BooleanUtils.isNotTrue(cartItem.getExported())) {
                cartItem.setExported(Boolean.TRUE);
                cartItem.persist();
            }
            Cart cart = cartItem.getCart();
            String couponCode = (cart.getCoupon() == null) ? "N/A" : cart.getCoupon().getCode();
            String color = (cartItem.getColor() == null) ? "N/A" : cartItem.getColor();
            String size = (cartItem.getColor() == null) ? "N/A" : cartItem.getSize();
            String eventTypeName = (cartItem.getEventType() == null) ? "N/A" : cartItem.getEventType().getTypeName();
            String categoryName = cartItem.getEventCartItemPriceChange() == null ? "All" : cartItem.getEventCartItemPriceChange().getCategoryName();
            EventCartItem eventCartItem = cartItem.getEventCartItem();
            UserProfile userProfile = cartItem.getUserProfile();
            String str = format.format(cartItem.getCreated()) + ", " + cartItem.getEventCartItem().getType() + ", " + cartItem.getEventCartItem().getName() + ", " 
            		+ cartItem.getQuantity() + ", " + size + ", " + color + ", " + couponCode + ", " 
                    + "$" + cartItem.getPrice() + ", " + categoryName + ", "
            		+ cart.isShared() + ", " ;
            if(quick) {
            	str="";
            }
                    str += eventTypeName + ", ";
            if(bibnum == true) {
            	if(cartItem.getEventCartItem().getType() == EventCartItemTypeEnum.TICKET) {
            		if (cartItem.getBib() == null) {
            			str += "-";
            		} else {
            			str += cartItem.getBib();
            		}
            	} else {
            		str += "N/A";
            	}
            }

            if (userProfile != null) {
            	String displayGender; 
            	if(userProfile.getGender().equalsIgnoreCase("male")) {
            		displayGender = "M";
            	} else if(userProfile.getGender().equalsIgnoreCase("female")) {
            		displayGender = "F";
            	} else {
            		displayGender = userProfile.getGender();
            	}
                str += userProfile.getFirstname() + ", " + userProfile.getLastname() + ", " + userProfile.getAge() + ", "
                		+ displayGender + ", " + userProfile.getEmail() + ", " + userProfile.getPhone() + ", "
                		+ userProfile.getAddressLine1().replace(",", "") + ", " + userProfile.getZipCode() + ", " + userProfile.getCity() + ", "
                		+ userProfile.getState() + ", " + userProfile.getEmergencyContactName() + ", " + userProfile.getEmergencyContactPhone();
            }
            if(questions == true) {
            	str += CustomRegFieldResponse.generateExportString(event, cart.getCustomRegFieldResponses(), fields);
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
