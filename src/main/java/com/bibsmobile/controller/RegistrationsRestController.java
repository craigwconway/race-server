package com.bibsmobile.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.bibsmobile.model.*;
import com.bibsmobile.util.UserProfileUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bibsmobile.util.JSONUtil;

@RequestMapping("/rest/registrations")
@Controller
public class RegistrationsRestController {

    @RequestMapping(value = "/search", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
    public ResponseEntity<String> search(@RequestParam("event") Long eventId, @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName, @RequestParam(value = "start", required = false) Integer start,
            @RequestParam(value = "count", required = false) Integer count, HttpServletRequest request) {
        try {
            // sanity check given parameters
            Event event = Event.findEvent(eventId);
            if (event == null)
                return new ResponseEntity<String>(JSONUtil.convertErrorMessage("event not found"), HttpStatus.NOT_FOUND);
            boolean firstNameGiven = (firstName != null && !firstName.isEmpty());
            boolean lastNameGiven = (lastName != null && !lastName.isEmpty());
            if (!(firstNameGiven || lastNameGiven))
                return new ResponseEntity<String>(JSONUtil.convertErrorMessage("firstName or lastName has to be included"), HttpStatus.BAD_REQUEST);

            // check the rights the user has for event
            UserAuthority authorityForEvent = null;
            UserProfile user = UserProfileUtil.getLoggedInUserProfile();
            if (user != null) {
                for (UserAuthorities ua : user.getUserAuthorities()) {
                    // sys admins have all permissions anyways
                    if (ua.getUserAuthority().isAuthority(UserAuthority.SYS_ADMIN)) {
                        authorityForEvent = ua.getUserAuthority();
                        break;
                    }
                    // check permissions specific to event
                    for (UserGroupUserAuthority ugua : ua.getUserGroupUserAuthorities()) {
                        for (EventUserGroup eug : ugua.getUserGroup().getEventUserGroups()) {
                            if (eug.getEvent().equals(event)) {
                                authorityForEvent = ua.getUserAuthority();
                                break;
                            }
                        }
                    }
                }
            }
            // sys admins have access in general, event admins if they are associated with the event (see search above)
            if (authorityForEvent == null || (!authorityForEvent.isAuthority(UserAuthority.SYS_ADMIN) && !authorityForEvent.isAuthority(UserAuthority.EVENT_ADMIN))) {
                return new ResponseEntity<String>(JSONUtil.convertErrorMessage("no rights for this event"), HttpStatus.UNAUTHORIZED);
            }

            // get relevant carts for event
            Set<Cart> carts = new HashSet<Cart>();
            List<EventCartItem> eventCartItems = EventCartItem.findEventCartItemsByEvent(event).getResultList();
            if (!eventCartItems.isEmpty()) {
                List<CartItem> cartItems = CartItem.findCompletedCartItemsByEventCartItems(eventCartItems, true).getResultList();
                for (CartItem cartItem : cartItems) {
                    carts.add(cartItem.getCart());
                }
            }

            // filter carts by first & last name
            List<ShortCart> registrations = new LinkedList<ShortCart>();
            for (Cart c : carts) {
                ShortCart r = new ShortCart(c);
                boolean foundUser = false;
                for (ShortCart.ShortCartItem ci : r.getCartItems()) {
                    if (firstNameGiven && lastNameGiven)
                        foundUser = ci.getUser().getFirstName() != null && ci.getUser().getFirstName().equalsIgnoreCase(firstName) && ci.getUser().getLastName() != null
                                && ci.getUser().getLastName().equalsIgnoreCase(lastName);
                    else if (firstNameGiven)
                        foundUser = ci.getUser().getFirstName() != null && ci.getUser().getFirstName().equalsIgnoreCase(firstName);
                    else if (lastNameGiven)
                        foundUser = ci.getUser().getLastName() != null && ci.getUser().getLastName().equalsIgnoreCase(lastName);
                }
                if (foundUser)
                    registrations.add(r);
            }

            return new ResponseEntity<String>(JSONUtil.convertPaginated(start, count, registrations), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<String>(JSONUtil.convertException(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

        public ShortCart(Cart c) {
            this.id = c.getId();
            this.created = c.getCreated();
            this.total = c.getTotal();
            this.status = c.getStatus();
            this.cartItems = new LinkedList<ShortCartItem>();
            for (CartItem ci : c.getCartItems()) {
                this.cartItems.add(new ShortCartItem(ci));
            }
        }

        @SuppressWarnings("unused")
        public Long getId() {
            return this.id;
        }

        @SuppressWarnings("unused")
        public Date getCreated() {
            return this.created;
        }

        @SuppressWarnings("unused")
        public double getTotal() {
            return this.total;
        }

        @SuppressWarnings("unused")
        public int getStatus() {
            return this.status;
        }

        public List<ShortCartItem> getCartItems() {
            return this.cartItems;
        }

        private static class ShortUser {
            private final Long id;
            private final String firstName;
            private final String lastName;
            private final Date birthDate;

            public ShortUser(UserProfile u) {
                this.id = u.getId();
                this.firstName = u.getFirstname();
                this.lastName = u.getLastname();
                this.birthDate = u.getBirthdate();
            }

            @SuppressWarnings("unused")
            public Long getId() {
                return this.id;
            }

            public String getFirstName() {
                return this.firstName;
            }

            public String getLastName() {
                return this.lastName;
            }

            @SuppressWarnings("unused")
            public Date getBirthdate() {
                return this.birthDate;
            }
        }

        private static class ShortCartItem {
            private final Long id;
            private final ShortUser user;
            private final int quantity;
            private final String color;
            private final String size;
            private final double price;

            public ShortCartItem(CartItem c) {
                this.id = c.getId();
                this.user = new ShortUser(c.getUserProfile());
                this.quantity = c.getQuantity();
                this.color = c.getColor();
                this.size = c.getSize();
                this.price = c.getPrice();
            }

            @SuppressWarnings("unused")
            public Long getId() {
                return this.id;
            }

            public ShortUser getUser() {
                return this.user;
            }

            @SuppressWarnings("unused")
            public int getQuantity() {
                return this.quantity;
            }

            @SuppressWarnings("unused")
            public String getColor() {
                return this.color;
            }

            @SuppressWarnings("unused")
            public String getSize() {
                return this.size;
            }

            @SuppressWarnings("unused")
            public double getPrice() {
                return this.price;
            }
        }
    }
}