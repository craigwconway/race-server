package com.bibsmobile.controller;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.MailgunUtil;
import com.bibsmobile.util.UserProfileUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.exception.APIException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.APIConnectionException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestMapping("/stripe")
@Controller
public class StripeController {
  private static final Logger log = LoggerFactory.getLogger(StripeController.class);

  @Value("${stripe.com.secret.key}")
  private String secretKey;

  private Customer upsertCustomer(UserProfile loggedInUser) throws CardException, InvalidRequestException, AuthenticationException, APIConnectionException, APIException {
    if (loggedInUser == null) return null;

    Map<String, Object> customerParams = new HashMap<String, Object>();
    customerParams.put("email", loggedInUser.getEmail());
    Map<String, Object> customerMetadata = new HashMap<String, Object>();
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
      if (stripeCustomer.getEmail() == null || !stripeCustomer.getEmail().equals(loggedInUser.getEmail())) changed = true;
      if (stripeCustomer.getMetadata() == null || !stripeCustomer.getMetadata().equals(customerMetadata)) changed = true;
      if (changed)
        stripeCustomer.update(customerParams);
    }
    return stripeCustomer;
  }

  private Card upsertCard(Customer stripeCustomer, String cardToken) throws CardException, InvalidRequestException, AuthenticationException, APIConnectionException, APIException {
    if (stripeCustomer == null) return null;

    if (stripeCustomer.getDefaultCard() != null)
      stripeCustomer.getCards().retrieve(stripeCustomer.getDefaultCard()).delete();

    if (cardToken != null) {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("card", cardToken);
      return stripeCustomer.createCard(params);
    }
    return null;
  }

  private Card upsertCard(UserProfile loggedInUser, String cardToken) throws CardException, InvalidRequestException, AuthenticationException, APIConnectionException, APIException {
    return this.upsertCard(this.upsertCustomer(loggedInUser), cardToken);
  }

  private String cardJson(Customer stripeCustomer) throws CardException, InvalidRequestException, AuthenticationException, APIConnectionException, APIException {
    if (stripeCustomer == null) return null;

    Card card = null;
    if (stripeCustomer.getDefaultCard() != null)
      card = stripeCustomer.getCards().retrieve(stripeCustomer.getDefaultCard());

    String cardJson = "{}";
    if (card != null)
      cardJson = "{\"brand\": \"" + card.getBrand() + "\", \"last4\": \"" + card.getLast4() + "\", \"exp_month\": \"" + card.getExpMonth() + "\", \"exp_year\": \"" + card.getExpYear() + "\"}";
    return cardJson;
  }

  private String cardJson(UserProfile loggedInUser) throws CardException, InvalidRequestException, AuthenticationException, APIConnectionException, APIException {
    return this.cardJson(this.upsertCustomer(loggedInUser));
  }

  @RequestMapping(value = "/card", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> customerCard(HttpServletRequest request) {
    // check login status
    UserProfile loggedInUser = UserProfileUtil.getLoggedInUserProfile();
    if (loggedInUser == null)
      return new ResponseEntity<String>("not logged in", HttpStatus.UNAUTHORIZED);

    // retrieve card
    Stripe.apiKey = secretKey;
    try {
      return new ResponseEntity<String>(this.cardJson(loggedInUser), HttpStatus.OK);
    } catch (CardException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    } catch (InvalidRequestException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (AuthenticationException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (APIConnectionException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (APIException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(value = "/card", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> customerCardChange(@RequestParam("stripeToken") String stripeCardToken, HttpServletRequest request) {
    // check login status
    UserProfile loggedInUser = UserProfileUtil.getLoggedInUserProfile();
    if (loggedInUser == null)
      return new ResponseEntity<String>("not logged in", HttpStatus.UNAUTHORIZED);

    Stripe.apiKey = secretKey;
    try {
      this.upsertCard(loggedInUser, stripeCardToken);
      return new ResponseEntity<String>(this.cardJson(loggedInUser), HttpStatus.OK);
    } catch (CardException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    } catch (InvalidRequestException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (AuthenticationException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (APIConnectionException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (APIException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(value = "/card", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<String> customerCardChange(HttpServletRequest request) {
    // check login status
    UserProfile loggedInUser = UserProfileUtil.getLoggedInUserProfile();
    if (loggedInUser == null)
      return new ResponseEntity<String>("not logged in", HttpStatus.UNAUTHORIZED);

    Stripe.apiKey = secretKey;
    try {
      this.upsertCard(loggedInUser, null);
      return new ResponseEntity<String>(this.cardJson(loggedInUser), HttpStatus.OK);
    } catch (CardException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    } catch (InvalidRequestException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (AuthenticationException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (APIConnectionException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (APIException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(value = "/chargeCard", method = RequestMethod.GET)
  public String chargeCardForm(HttpServletRequest request) {
    return "stripe/form";
  }

  @RequestMapping(value = "/chargeCard", method = RequestMethod.POST, headers = "Accept=application/json")
  @ResponseBody
  public ResponseEntity<String> chargeCardProcessingJson(@RequestBody String body, HttpServletRequest request) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ChargeCardBody parsedJson = mapper.readValue(body, ChargeCardBody.class);
    Long cartId = parsedJson.getCart();
    String stripeCardToken = parsedJson.getCard();
    boolean rememberCard = parsedJson.getRememberCard();
    return this.chargeCardProcessing(cartId, stripeCardToken, rememberCard, request);
  }

  @RequestMapping(value = "/chargeCardForm", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> chargeCardProcessingForm(@RequestParam("cart") Long cartId, @RequestParam(value="stripeToken", required=false) String stripeCardToken, @RequestParam(value="rememberCard", required=false) boolean rememberCard, HttpServletRequest request) throws IOException {
    return this.chargeCardProcessing(cartId, stripeCardToken, rememberCard, request);
  }

  public ResponseEntity<String> chargeCardProcessing(Long cartId, String stripeCardToken, boolean rememberCard, HttpServletRequest request) throws IOException {
    Cart c = Cart.findCart(cartId);
    if (c == null)
      return new ResponseEntity<String>("cart not found", HttpStatus.NOT_FOUND);
    if (c.getStatus() != Cart.NEW && c.getStatus() != Cart.SAVED)
      return new ResponseEntity<String>("cart already processed", HttpStatus.BAD_REQUEST);

    // in try block, so we can reset cart on failure
    try {
      // set cart to processing, so it doesn't get messed with (cart timeout)
      c.setStatus(Cart.PROCESSING);
      c.persist();

      // TODO do not save price in double
      double cartTotal = c.getTotal();
      long cartTotalCents = Math.round(cartTotal * 100);

      // needs to be logged in, if
      // 1) no card token was submitted
      // 2) card should be remembered
      // failure if no card token provided and no customer saved
      UserProfile loggedInUser = UserProfileUtil.getLoggedInUserProfile();
      if (stripeCardToken == null && loggedInUser == null)
        return new ResponseEntity<String>("not logged in", HttpStatus.UNAUTHORIZED);
      if (rememberCard && loggedInUser == null)
        return new ResponseEntity<String>("not logged in", HttpStatus.UNAUTHORIZED);
      if (stripeCardToken == null && loggedInUser.getStripeCustomerId() == null)
        return new ResponseEntity<String>("no credit card saved", HttpStatus.BAD_REQUEST);

      // stripe stuff
      Stripe.apiKey = secretKey;

      // saving customer if requested
      if (rememberCard) {
        try {
          this.upsertCard(loggedInUser, stripeCardToken);
        } catch (CardException e) {
          return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (InvalidRequestException e) {
          return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException e) {
          return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (APIConnectionException e) {
          return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (APIException e) {
          return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }

      Map<String, Object> chargeParams = new HashMap<String, Object>();
      chargeParams.put("amount", cartTotalCents);
      chargeParams.put("currency", "usd"); // TODO hardcoded for now
      // use customer or card token
      if (stripeCardToken == null || rememberCard) {
        chargeParams.put("customer", loggedInUser.getStripeCustomerId());
      } else {
        chargeParams.put("card", stripeCardToken);
      }
      chargeParams.put("description", "Charge for Cart " + c.getId());
      Map<String, String> initialMetadata = new HashMap<String, String>();
      initialMetadata.put("order_id", c.getId().toString());
      chargeParams.put("metadata", initialMetadata);

      Charge stripeCharge = null;
      try {
        stripeCharge = Charge.create(chargeParams);
      } catch (CardException e) {
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
      } catch (InvalidRequestException e) {
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (AuthenticationException e) {
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (APIConnectionException e) {
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (StripeException e) {
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }

      if (stripeCharge != null) {
        c.setStatus(Cart.COMPLETE);
        c.setStripeChargeId(stripeCharge.getId());
        c.persist();

        // can only sent an email if we have a logged in users email
        log.error("################################## loggedInUser: " + loggedInUser);
        if (loggedInUser != null) {
          // this assumes that all cartitems belong to the same event, so only grabbing first one
          Event cartEvent = c.getCartItems().get(0).getEventCartItem().getEvent();

          // built text for order confirmation mail
          String resultString = new String();
          resultString = "Hey " + loggedInUser.getFirstname() + ",\n";
          resultString += "Thank you for registering for ";
          resultString += cartEvent.getName(); //grab event with associated items here
          resultString += " using bibs.";
          resultString += "Your total comes to ";
          //TODO: Handle different types of currency here in the future.
          resultString += "$" + cartTotal + ":\n";
          for (CartItem ci : c.getCartItems()) {
            resultString += "- " + ci.getEventCartItem().getName() + ": " + ci.getEventCartItem().getDescription() + "\n";
          }
          resultString += "\n";
          SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
          resultString += "See you on " + sdf.format(cartEvent.getTimeStart()) + "!\n";
          resultString += "- the bibs team";
          MailgunUtil.send(loggedInUser.getEmail(), "Thank you for registering with bibs!", resultString);
        }

        return new ResponseEntity<String>("card charged", HttpStatus.OK);
      } else {
        return new ResponseEntity<String>("card charge failed", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } finally {
      // reset card to initial state in case payment failed
      if (c.getStatus() == Cart.PROCESSING) {
        c.setStatus(Cart.NEW);
        c.persist();
      }
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  protected static class ChargeCardBody {
    private Long cart = null; // shopping cart
    private String card = null; // CC
    private Boolean rememberCard = null;
    public Long getCart() { return this.cart; }
    public String getCard() { return this.card; }
    public boolean getRememberCard() { return this.rememberCard != null && this.rememberCard.booleanValue(); }
  }
}
