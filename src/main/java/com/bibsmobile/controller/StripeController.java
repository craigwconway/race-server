package com.bibsmobile.controller;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.MailgunUtil;
import com.bibsmobile.util.RegistrationsUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Refund;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/stripe")
@Controller
public class StripeController {

    @Value("${stripe.com.secret.key}")
    private String secretKey;

    private Customer upsertCustomer(UserProfile loggedInUser) throws CardException, InvalidRequestException, AuthenticationException, APIConnectionException, APIException {
        if (loggedInUser == null)
            return null;

        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("email", loggedInUser.getEmail());
        Map<String, Object> customerMetadata = new HashMap<>();
        customerMetadata.put("bibsID", loggedInUser.getId());
        customerParams.put("metadata", customerMetadata);

        Customer stripeCustomer = null;
        if (loggedInUser.getStripeCustomerId() == null) { // new stripe customer
            stripeCustomer = Customer.create(customerParams);
            loggedInUser.setStripeCustomerId(stripeCustomer.getId());
            loggedInUser.persist();
        } else { // update existing
            stripeCustomer = Customer.retrieve(loggedInUser.getStripeCustomerId());
            boolean changed = false;
            if (stripeCustomer.getEmail() == null || !stripeCustomer.getEmail().equals(loggedInUser.getEmail()))
                changed = true;
            if (stripeCustomer.getMetadata() == null || !stripeCustomer.getMetadata().equals(customerMetadata))
                changed = true;
            if (changed)
                stripeCustomer.update(customerParams);
        }
        return stripeCustomer;
    }

    private Card upsertCard(Customer stripeCustomer, String cardToken) throws CardException, InvalidRequestException, AuthenticationException, APIConnectionException, APIException {
        if (stripeCustomer == null)
            return null;

        if (stripeCustomer.getDefaultCard() != null)
            stripeCustomer.getCards().retrieve(stripeCustomer.getDefaultCard()).delete();

        if (cardToken != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("card", cardToken);
            return stripeCustomer.createCard(params);
        }
        return null;
    }

    private Card upsertCard(UserProfile loggedInUser, String cardToken) throws CardException, InvalidRequestException, AuthenticationException, APIConnectionException,
            APIException {
        return this.upsertCard(this.upsertCustomer(loggedInUser), cardToken);
    }

    private String cardJson(Customer stripeCustomer) throws CardException, InvalidRequestException, AuthenticationException, APIConnectionException, APIException {
        if (stripeCustomer == null)
            return null;

        Card card = null;
        if (stripeCustomer.getDefaultCard() != null)
            card = stripeCustomer.getCards().retrieve(stripeCustomer.getDefaultCard());

        String cardJson = "{}";
        if (card != null)
            cardJson = "{\"brand\": \"" + card.getBrand() + "\", \"last4\": \"" + card.getLast4() + "\", \"exp_month\": \"" + card.getExpMonth() + "\", \"exp_year\": \""
                    + card.getExpYear() + "\"}";
        return cardJson;
    }

    private String cardJson(UserProfile loggedInUser) throws CardException, InvalidRequestException, AuthenticationException, APIConnectionException, APIException {
        return this.cardJson(this.upsertCustomer(loggedInUser));
    }

    @RequestMapping(value = "/card", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> customerCard() {
        // check login status
        UserProfile loggedInUser = UserProfileUtil.getLoggedInUserProfile();
        if (loggedInUser == null)
            return new ResponseEntity<>("not logged in", HttpStatus.UNAUTHORIZED);

        // retrieve card
        Stripe.apiKey = this.secretKey;
        try {
            return new ResponseEntity<>(this.cardJson(loggedInUser), HttpStatus.OK);
        } catch (CardException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (APIConnectionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (APIException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/card", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> customerCardChange(@RequestParam("stripeToken") String stripeCardToken) {
        // check login status
        UserProfile loggedInUser = UserProfileUtil.getLoggedInUserProfile();
        if (loggedInUser == null)
            return new ResponseEntity<>("not logged in", HttpStatus.UNAUTHORIZED);

        Stripe.apiKey = this.secretKey;
        try {
            this.upsertCard(loggedInUser, stripeCardToken);
            return new ResponseEntity<>(this.cardJson(loggedInUser), HttpStatus.OK);
        } catch (CardException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (APIConnectionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (APIException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/card", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> customerCardChange() {
        // check login status
        UserProfile loggedInUser = UserProfileUtil.getLoggedInUserProfile();
        if (loggedInUser == null)
            return new ResponseEntity<>("not logged in", HttpStatus.UNAUTHORIZED);

        Stripe.apiKey = this.secretKey;
        try {
            this.upsertCard(loggedInUser, null);
            return new ResponseEntity<>(this.cardJson(loggedInUser), HttpStatus.OK);
        } catch (CardException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (APIConnectionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (APIException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/chargeCard", method = RequestMethod.GET)
    public String chargeCardForm() {
        return "stripe/form";
    }

    @RequestMapping(value = "/chargeCard", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> chargeCardProcessingJson(@RequestBody String body) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ChargeCardBody parsedJson = mapper.readValue(body, ChargeCardBody.class);
        Long cartId = parsedJson.getCart();
        String stripeCardToken = parsedJson.getCard();
        boolean rememberCard = parsedJson.getRememberCard();
        return this.chargeCardProcessing(cartId, stripeCardToken, rememberCard);
    }

    @RequestMapping(value = "/chargeCardForm", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> chargeCardProcessingForm(@RequestParam("cart") Long cartId, @RequestParam(value = "stripeToken", required = false) String stripeCardToken,
            @RequestParam(value = "rememberCard", required = false) boolean rememberCard) {
        return this.chargeCardProcessing(cartId, stripeCardToken, rememberCard);
    }

    public ResponseEntity<String> chargeCardProcessing(Long cartId, String stripeCardToken, boolean rememberCard) {
        Cart c = Cart.findCart(cartId);
        if (c == null)
            return new ResponseEntity<>("cart not found", HttpStatus.NOT_FOUND);
        if (c.getStatus() != Cart.NEW && c.getStatus() != Cart.SAVED)
            return new ResponseEntity<>("cart already processed", HttpStatus.BAD_REQUEST);

        // in try block, so we can reset cart on failure
        try {
            // set cart to processing, so it doesn't get messed with (cart
            // timeout)
            c.setStatus(Cart.PROCESSING);
            c.persist();

            // HACK
            // TODO: this better
            long cartTotalCents = c.getTotal() * 100;
            System.out.println("This is our price: " + c.getTotal());
            // needs to be logged in, if
            // 1) no card token was submitted
            // 2) card should be remembered
            // failure if no card token provided and no customer saved
            UserProfile loggedInUser = UserProfileUtil.getLoggedInUserProfile();
            
            if (stripeCardToken == null && loggedInUser == null)
                return new ResponseEntity<>("not logged in", HttpStatus.UNAUTHORIZED);
            if (rememberCard && loggedInUser == null)
                return new ResponseEntity<>("not logged in", HttpStatus.UNAUTHORIZED);
            if (stripeCardToken == null && loggedInUser.getStripeCustomerId() == null)
                return new ResponseEntity<>("no credit card saved", HttpStatus.BAD_REQUEST);

            // stripe stuff
            Stripe.apiKey = this.secretKey;

            // saving customer if requested
            if (rememberCard) {
                try {
                    this.upsertCard(loggedInUser, stripeCardToken);
                } catch (CardException e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
                } catch (InvalidRequestException e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (AuthenticationException e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (APIConnectionException e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (APIException e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", cartTotalCents);
            chargeParams.put("currency", "usd"); // TODO hardcoded for now
            // use customer or card token
            if (stripeCardToken == null || rememberCard) {
                chargeParams.put("customer", loggedInUser.getStripeCustomerId());
            } else {
                chargeParams.put("card", stripeCardToken);
            }
            chargeParams.put("description", "Charge for Cart " + c.getId());
            Map<String, String> initialMetadata = new HashMap<>();
            initialMetadata.put("order_id", c.getId().toString());
            chargeParams.put("metadata", initialMetadata);

            Charge stripeCharge = null;
            try {
                stripeCharge = Charge.create(chargeParams);
            } catch (CardException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
            } catch (InvalidRequestException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (AuthenticationException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (APIConnectionException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (StripeException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (stripeCharge != null) {
                c.setStatus(Cart.COMPLETE);
                c.setStripeChargeId(stripeCharge.getId());
                c.persist();

                // can only sent an email if we have a logged in users email
                if (loggedInUser != null) {
                    // this assumes that all cartitems belong to the same event,
                    // so only grabbing first one
                    Event cartEvent = c.getCartItems().get(0).getEventCartItem().getEvent();

                    // built text for order confirmation mail
                    String resultString = new String();
                    resultString = "Hey " + loggedInUser.getFirstname() + ",\n";
                    resultString += "Thank you for registering for ";
                    resultString += cartEvent.getName(); // grab event with
                                                         // associated items
                                                         // here
                    resultString += " using bibs.";
                    resultString += "Your total comes to ";
                    // TODO: Handle different types of currency here in the
                    // future.
                    resultString += "$" + (cartTotalCents / 100) + "." + (cartTotalCents % 100) + ":\n";
                    for (CartItem ci : c.getCartItems()) {
                        resultString += "- " + ci.getEventCartItem().getName() + ": " + ci.getEventCartItem().getDescription() + "\n";
                    }
                    resultString += "\n";
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    resultString += "See you on " + sdf.format(cartEvent.getTimeStart()) + "!\n";
                    resultString += "- the bibs team";
                    resultString += "\n\nIssues? Your transaction id is: B" + c.getId() + "T" + c.getStripeChargeId();
                    MailgunUtil.send(loggedInUser.getEmail(), "Thank you for registering with bibs!", resultString);
                }

                return new ResponseEntity<>("card charged", HttpStatus.OK);
            }
            return new ResponseEntity<>("card charge failed", HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            // reset card to initial state in case payment failed
            if (c.getStatus() == Cart.PROCESSING) {
                c.setStatus(Cart.NEW);
                c.persist();
            }
        }
    }

    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public ResponseEntity<String> refund(@RequestParam("invoice") String invoiceId,
                                         @RequestParam("firstName") String firstName,
                                         @RequestParam("email") String email) {
        // stripe stuff
        Stripe.apiKey = this.secretKey;

        // mark cart that refund was started
        Cart cart = RegistrationsUtil.findCartFromInvoice(invoiceId, firstName, email);
        if (cart == null) return SpringJSONUtil.returnErrorMessage("unknown cart", HttpStatus.BAD_REQUEST);
        cart.setStatus(Cart.REFUND_REQUEST);
        cart.persist();

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("reason", "requested_by_customer");
            Charge stripeCharge = Charge.retrieve(cart.getStripeChargeId());
            Refund stripeRefund = stripeCharge.getRefunds().create(params);
            cart.setStripeRefundId(stripeRefund.getId());
            cart.persist();
        } catch (APIConnectionException | AuthenticationException | APIException | CardException | InvalidRequestException e) {
            return SpringJSONUtil.returnException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            // refund successful
            cart.setStatus(Cart.REFUNDED);
            cart.persist();
        }
        return SpringJSONUtil.returnStatusMessage("refund successful", HttpStatus.OK);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class ChargeCardBody {
        private final Long cart = null; // shopping cart
        private final String card = null; // CC
        private final Boolean rememberCard = null;

        public Long getCart() {
            return this.cart;
        }

        public String getCard() {
            return this.card;
        }

        public boolean getRememberCard() {
            return this.rememberCard != null && this.rememberCard.booleanValue();
        }
    }
}
