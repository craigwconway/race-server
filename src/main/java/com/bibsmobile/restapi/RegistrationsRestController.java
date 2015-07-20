package com.bibsmobile.restapi;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.PermissionsUtil;
import com.bibsmobile.util.RegistrationsUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@RequestMapping("/rest/registrations")
@Controller
public class RegistrationsRestController {
	
	private static final Logger log = LoggerFactory.getLogger(RegistrationsRestController.class);

    @RequestMapping(value = "/search", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<String> search(@RequestParam("event") Long eventId,
                                         @RequestParam(value = "firstName", required = false) String firstName,
                                         @RequestParam(value = "lastName", required = false) String lastName,
                                         @RequestParam(value = "start", required = false) Integer start,
                                         @RequestParam(value = "count", required = false) Integer count) {
        try {
            // sanity check given parameters
            Event event = Event.findEvent(eventId);
            log.info("Search Registrations: event:"+ eventId + " firstname:" + firstName + " lastname:" + lastName + " start:" + start + " count:" + count);
            if (event == null)
                return SpringJSONUtil.returnErrorMessage("event not found", HttpStatus.NOT_FOUND);

            // check the rights the user has for event
            if (!PermissionsUtil.isEventAdmin(UserProfileUtil.getLoggedInUserProfile(), event)) {
                return SpringJSONUtil.returnErrorMessage("no rights for this event", HttpStatus.UNAUTHORIZED);
            }

            List<Cart> registrationsFull = RegistrationsUtil.search(event, firstName, lastName, null, null);
            if (registrationsFull == null)
                return SpringJSONUtil.returnErrorMessage("firstName or lastName has to be included", HttpStatus.BAD_REQUEST);
            List<ShortCart> registrations = new ArrayList<ShortCart>(registrationsFull.size());
            for (Cart c : registrationsFull)
                registrations.add(new ShortCart(c));

            return SpringJSONUtil.returnPaginated(start, count, registrations, HttpStatus.OK);
        } catch (Exception e) {
            return SpringJSONUtil.returnException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * @api {put} /rest/registrations/edit/:id
     * @apiName editRegistration
     * @apiParam {Object} user UserProfile object in cart
     * @apiParam {String} user.firstname First Name of user in cart
     * @apiParam {String} user.lastname Last Name of user in cart
     * @apiParam {String} user.email Email of user in cart
     * @apiParam {String} user.emergencyContactName Name of emergency contact
     * @apiParam {String} user.emergencyContactPhone Phone number of emergency contact
     * @apiParam {String} user.birthdate Birthdate of user
     * @apiParam {Object[]} [cartitems] Array of CartItem objects to change
     * @apiGroup registrations
     * @apiParamExample {json} Request-Example
     * 		{
     * 			"user": {
     * 				"id": 7,
     * 				"firstname": "bob",
     * 				"lastname": "dylan",
     * 				"email": "lsdkfj@sdklj.dsf",
     * 				"phone": "2390480248",
     * 				"emergencyContactName": "someone",
     * 				"emergencyContactPhone": "9119119111",
     * 				"birthdate": "1990-01-01"
     * 			},
     * 			"cartItems": [
     * 				{
     * 					"id": 1,
     * 					"color": null,
     * 					"size": null
     * 				},
     * 				{
     * 					"id": 2,
     * 					"color": "Yellow",
     * 					"size": "M"
     * 				}
     * 			]
     * 		}
     * @apiParamExample {json} Request Example 2
     * 		{ 
     * 		"user": 
     * 			{ 
     * 			  "firstname":"galen",
     * 			  "lastname":"danziger",
     * 			  "emergencyContactName":"nathan",
     * 			  "emergencyContactPhone":"1231231231",
     *			  "gender":"M", 
     *			  "email":"gedanziger@gmail.com"
     *			},
     *		"cartItems": [{"id":2, "color":"yellow", "size":"M"}]
     *		}
     * @apiSuccess (200) No Response.
     */
    @RequestMapping(value = "edit/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody Cart cart, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        //Cart cart = Cart.fromJsonToCart(json);
        Cart trueCart = Cart.findCart(id);
        if(trueCart == null) {
        	log.error("Attempt to edit missing cart id: " + id);
        	return new ResponseEntity<> (headers, HttpStatus.NOT_FOUND);
        }
     
        
        List<CartItem> cartItems = cart.getCartItems();
        List<CartItem> trueCartItems = trueCart.getCartItems();
        
        log.info("Editing cart " + trueCart);
        
        // check the rights the user has for event
        if (!PermissionsUtil.isEventAdmin(UserProfileUtil.getLoggedInUserProfile(), trueCart.getCartItems().get(0).getEventCartItem().getEvent())) {
        	log.warn("Permission mismatch on attempted cart edit");
            return SpringJSONUtil.returnErrorMessage("no rights for this event", HttpStatus.UNAUTHORIZED);
        }
        //Check incoming userprofile, if it is edited create a new regularuser:
        UserProfile newUserProfile = new UserProfile();
        newUserProfile.setFirstname(cart.getUser().getFirstname());
        newUserProfile.setLastname(cart.getUser().getLastname());
        newUserProfile.setGender(cart.getUser().getGender());
        newUserProfile.setEmail(cart.getUser().getEmail());
        newUserProfile.setPhone(cart.getUser().getPhone());
        newUserProfile.setEmergencyContactName(cart.getUser().getEmergencyContactName());
        newUserProfile.setEmergencyContactPhone(cart.getUser().getEmergencyContactPhone());
        newUserProfile.setBirthdate(cart.getUser().getBirthdate());

        UserProfile currentUserProfile = trueCart.getUser();
        
        if(currentUserProfile.getFirstname() != newUserProfile.getFirstname()
        		|| currentUserProfile.getLastname() != newUserProfile.getLastname()
        		|| currentUserProfile.getEmail() != newUserProfile.getEmail()
        		|| currentUserProfile.getPhone() != newUserProfile.getPhone()
        		|| currentUserProfile.getEmergencyContactName() != newUserProfile.getEmergencyContactName()
        		|| currentUserProfile.getEmergencyContactPhone() != newUserProfile.getEmergencyContactPhone()
        		|| currentUserProfile.getBirthdate() != newUserProfile.getBirthdate()
        		|| currentUserProfile.getGender() != newUserProfile.getGender()
        		) {
	        newUserProfile.persist();
	        log.info("Updating cart " + cart.getId() + " Old user: " + cart.getUser().getId() + " New user: " + newUserProfile.getId());
	        trueCart.setUser(newUserProfile);
	        trueCart.merge();	
        }
       
        for(CartItem tci : trueCartItems) {
        	for(CartItem ci : cart.getCartItems()) {
        		if(tci.getId() == ci.getId()) {
        			if(tci.getColor() != ci.getColor() || tci.getSize() != ci.getSize()) {
        				log.info("Updating sizing for item " + tci.getId() + ". Color: " + tci.getColor() + "->" + ci.getColor() +
        						" Size: " + tci.getSize() + "->" + ci.getSize());
        			}
        			// Only allow safe edits for now, color/size.
        			// In the future, we will change the event type mapping from cartItem -> eventType for export purposes
        			tci.setColor(ci.getColor());
        			tci.setSize(ci.getSize());
        		}
        	}
			tci.setUserProfile(trueCart.getUser());
			tci.merge();
			log.info("Updating cart item Id: " + tci.getId() + " Color: " + tci.getColor() + " Size: " + tci.getSize());
        }
        /*
        if(null != cartItems) {
	        for(CartItem cartItem: cartItems) {
	        	System.out.println("updatingcartitem: " + cartItem);
	        	cartItem.setUserProfile(cart.getUser());
	        	cartItem.persist();
	        }
        }
        if (cart.merge() == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        */
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    
    // TODO remove
    @RequestMapping(value = "/transfer", method = RequestMethod.GET)
    public String transferForm(@RequestParam("invoice") String invoiceId,
                               @RequestParam("firstName") String firstName,
                               @RequestParam("email") String email,
                               Model uiModel) {
    	try {
            CartItem cartItem = RegistrationsUtil.findCartItemFromInvoice(invoiceId, firstName, email);
            if (cartItem == null) throw new IllegalArgumentException("CartItem not found");
            uiModel.addAttribute("firstname", cartItem.getUserProfile().getFirstname());
            uiModel.addAttribute("lastname", cartItem.getUserProfile().getLastname());
            uiModel.addAttribute("email", cartItem.getUserProfile().getEmail());
            uiModel.addAttribute("city", cartItem.getUserProfile().getCity());
            uiModel.addAttribute("state", cartItem.getUserProfile().getState());
            uiModel.addAttribute("phone", cartItem.getUserProfile().getPhone());
            uiModel.addAttribute("emergencycontactname", cartItem.getUserProfile().getEmergencyContactName());
            uiModel.addAttribute("emergencycontactphone", cartItem.getUserProfile().getEmergencyContactPhone());
            uiModel.addAttribute("birthdate", cartItem.getUserProfile().getBirthdate());
            return "registrations/transfer";   		
    	} catch(Exception e) {
    		log.warn("Invalid transfer request form. Invoice: " + invoiceId + " firstname: " + firstName + " email: " + email);
    		return "registrations/transfererror";
    	}

    }

    @RequestMapping(value = "/transfer", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> transfer(@RequestParam("invoice") String invoiceId,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("email") String email,
                                           @RequestBody String newUserStr) {
        CartItem cartItem = RegistrationsUtil.findCartItemFromInvoice(invoiceId, firstName, email);
        if (cartItem == null) return SpringJSONUtil.returnErrorMessage("unknown cart", HttpStatus.BAD_REQUEST);
        if (!cartItem.getEventCartItem().getEvent().isTicketTransferEnabled())
            return SpringJSONUtil.returnErrorMessage("ticket transfer disabled", HttpStatus.FORBIDDEN);

           // create or get user profile
        ShortUser newUser = null;
        try {
            newUser = new ObjectMapper().readValue(newUserStr, ShortUser.class);
        } catch (IOException e) {
            return SpringJSONUtil.returnException(e, HttpStatus.BAD_REQUEST);
        }
        UserProfile newUserProfile;
        if (newUser.getId() == null) {
            newUserProfile = new UserProfile();
            newUserProfile.setFirstname(newUser.getFirstName());
            newUserProfile.setLastname(newUser.getLastName());
            newUserProfile.setEmail(newUser.getEmail());
            newUserProfile.setPhone(newUser.getPhone());
            newUserProfile.setEmergencyContactName(newUser.getEmergencyContactName());
            newUserProfile.setEmergencyContactPhone(newUser.getEmergencyContactPhone());
            newUserProfile.setBirthdate(newUser.getBirthdate());
            newUserProfile.persist();
        } else {
            newUserProfile = UserProfile.findUserProfile(newUser.getId());
            if (newUserProfile == null) {
            	log.warn("Attempt to transfer to invalid user");
            	return SpringJSONUtil.returnErrorMessage("unknown user id", HttpStatus.BAD_REQUEST);
            }
        }

        // change user in the cart
        Cart cart = cartItem.getCart();
        cartItem.setUserProfile(newUserProfile);
        cartItem.persist();
        cart.setUser(newUserProfile);
        cart.merge();
        log.info("Ticket transfer: Cart " + cart.getId() + " to user: " + newUserProfile.getId());
        
        return SpringJSONUtil.returnObject(new ShortCart(cartItem.getCart()), HttpStatus.OK);
    }

    /**
     * JSON output objects all the getters are needed by the Jackson json
     * serializer, but the compiler doesn't know that
     */
    private static class ShortCart {
        private final Long id;
        private final Date created;
        private final double total;
        private final int status;
        private final List<ShortCartItem> cartItems;

        protected ShortCart(Cart c) {
            super();
            this.id = c.getId();
            this.created = c.getCreated();
            this.total = c.getTotal();
            this.status = c.getStatus();
            this.cartItems = new LinkedList<>();
            for (CartItem ci : c.getCartItems()) {
                this.cartItems.add(new ShortCartItem(ci));
            }
        }

        public Long getId() {
            return this.id;
        }

        public Date getCreated() {
            return this.created;
        }

        public double getTotal() {
            return this.total;
        }

        public int getStatus() {
            return this.status;
        }

        public List<ShortCartItem> getCartItems() {
            return this.cartItems;
        }
    }

    private static class ShortUser {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String emergencyContactName;
        private String emergencyContactPhone;
        private Date birthDate;

        protected ShortUser() {
            super();
        }

        protected ShortUser(UserProfile u) {
            super();
            this.id = u.getId();
            this.firstName = u.getFirstname();
            this.lastName = u.getLastname();
            this.email = u.getEmail();
            this.phone = u.getPhone();
            this.emergencyContactName = u.getEmergencyContactName();
            this.emergencyContactPhone = u.getEmergencyContactPhone();
            this.birthDate = u.getBirthdate();
        }

        public Long getId() {
            return this.id;
        }

        public String getFirstName() {
            return this.firstName;
        }

        public String getLastName() {
            return this.lastName;
        }

        public String getEmail() {
            return this.email;
        }

        public String getPhone() {
            return this.phone;
        }

        public String getEmergencyContactName() {
            return this.emergencyContactName;
        }

        public String getEmergencyContactPhone() {
            return this.emergencyContactPhone;
        }

        public Date getBirthdate() {
            return this.birthDate;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setEmergencyContactName(String emergencyContactName) {
            this.emergencyContactName = emergencyContactName;
        }

        public void setEmergencyContactPhone(String emergencyContactPhone) {
            this.emergencyContactPhone = emergencyContactPhone;
        }

        public void setBirthDate(Date birthDate) {
            this.birthDate = birthDate;
        }
    }

    private static class ShortCartItem {
        private final Long id;
        private final ShortUser user;
        private final int quantity;
        private final String color;
        private final String size;
        private final double price;

        protected ShortCartItem(CartItem c) {
            super();
            this.id = c.getId();
            this.user = new ShortUser(c.getUserProfile());
            this.quantity = c.getQuantity();
            this.color = c.getColor();
            this.size = c.getSize();
            this.price = c.getPrice();
        }

        public Long getId() {
            return this.id;
        }

        public ShortUser getUser() {
            return this.user;
        }

        public int getQuantity() {
            return this.quantity;
        }

        public String getColor() {
            return this.color;
        }

        public String getSize() {
            return this.size;
        }

        public double getPrice() {
            return this.price;
        }
    }
}
