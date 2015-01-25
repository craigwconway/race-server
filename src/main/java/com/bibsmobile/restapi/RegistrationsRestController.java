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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @RequestMapping(value = "/search", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<String> search(@RequestParam("event") Long eventId,
                                         @RequestParam(value = "firstName", required = false) String firstName,
                                         @RequestParam(value = "lastName", required = false) String lastName,
                                         @RequestParam(value = "start", required = false) Integer start,
                                         @RequestParam(value = "count", required = false) Integer count) {
        try {
            // sanity check given parameters
            Event event = Event.findEvent(eventId);
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
            uiModel.addAttribute("phone", cartItem.getUserProfile().getPhone());
            uiModel.addAttribute("emergencycontactname", cartItem.getUserProfile().getEmergencyContactName());
            uiModel.addAttribute("emergencycontactphone", cartItem.getUserProfile().getEmergencyContactPhone());
            uiModel.addAttribute("birthdate", cartItem.getUserProfile().getBirthdate());
            return "registrations/transfer";   		
    	} catch(Exception e) {
    		System.out.println(e);
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

        // check the rights the user has for event
        if (!PermissionsUtil.isEventAdmin(UserProfileUtil.getLoggedInUserProfile(), cartItem.getEventCartItem().getEvent())) {
            return SpringJSONUtil.returnErrorMessage("no rights for this event", HttpStatus.UNAUTHORIZED);
        }

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
            if (newUserProfile == null) return SpringJSONUtil.returnErrorMessage("unknown user id", HttpStatus.BAD_REQUEST);
        }

        // change user in the cart
        cartItem.setUserProfile(newUserProfile);
        cartItem.persist();
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
