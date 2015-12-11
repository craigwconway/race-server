package com.bibsmobile.controller;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemTypeEnum;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger log = LoggerFactory.getLogger(StripeController.class);

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

    @RequestMapping(value = "/freeCart", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> freeCardProcessingJson(@RequestBody String body) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ChargeCardBody parsedJson = mapper.readValue(body, ChargeCardBody.class);
        Long cartId = parsedJson.getCart();
        return this.freeCartProcessing(cartId);
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
        UserProfile loggedInUser = UserProfileUtil.getLoggedInUserProfile();
        if(loggedInUser == null) {
        	return new ResponseEntity<>("Invalid User Data", HttpStatus.BAD_REQUEST);
        }
        System.out.println("[Stripe] Handling Charge for cart: " + c.getId() + ", Total: " + c.getTotal() + " User: " + loggedInUser.getEmail());
        // in try block, so we can reset cart on failure
        try {
            // set cart to processing, so it doesn't get messed with (cart
            // timeout)
            c.setStatus(Cart.PROCESSING);
            c.persist();

            // HACK
            // TODO: this better
            long cartTotalCents = c.getTotal();
            // needs to be logged in, if
            // 1) no card token was submitted
            // 2) card should be remembered
            // failure if no card token provided and no customer saved
            // HACK FOR CONFERENCE:
            if (loggedInUser != null) {
            	c.setUser(loggedInUser);
            	for(CartItem ci : c.getCartItems()) {
            		ci.setUserProfile(loggedInUser);
            		ci.persist();
            	}
            	c.persist();
            }
            // END OF HACK FOR CONFRENECE:
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
            	log.info("Error charging cart id: " + c.getId() + " card unauthorized");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
            } catch (InvalidRequestException e) {
            	log.error("Error charging cart id: " + c.getId() + " Invalid request " + e);
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (AuthenticationException e) {
            	log.error("Error charging cart id: " + c.getId() + " Authentication exception " + e);
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (APIConnectionException e) {
            	log.error("Error charging cart id: " + c.getId() + " API Connection Exception " + e);
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (StripeException e) {
            	log.error("Error charging cart id: " + c.getId() + " Stripe Exception " + e);
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (stripeCharge != null) {
                c.setStatus(Cart.COMPLETE);
                c.setStripeChargeId(stripeCharge.getId());
                c.persist();
                log.info("Completed charge for cart Id: " + c.getId() + " total: " + c.getTotal());
                //Review this plz
                for(CartItem ci : c.getCartItems()) {
                	EventCartItem eciUpdate = ci.getEventCartItem();
                	if (eciUpdate.getType() == EventCartItemTypeEnum.DONATION) {
                		eciUpdate.setPurchased(eciUpdate.getPurchased() + 1);
                	} else {
                    	eciUpdate.setPurchased(eciUpdate.getPurchased() + ci.getQuantity());
                	}
                	eciUpdate.merge();
                }

                // can only sent an email if we have a logged in users email
                if (loggedInUser != null) {
                    // this assumes that all cartitems belong to the same event,
                    // so only grabbing first one
                    Event cartEvent = c.getCartItems().get(0).getEventCartItem().getEvent();

                    // built text for order confirmation mail
                    String resultString = new String();
                    resultString = MailgunUtil.REG_RECEIPT_ONE;
                    resultString += cartEvent.getName();
                    resultString += MailgunUtil.REG_RECEIPT_TWO;
                    resultString += loggedInUser.getFirstname();
                    resultString += MailgunUtil.REG_RECEIPT_THREE;
                    resultString += cartEvent.getName();
                    resultString += MailgunUtil.REG_RECEIPT_FOUR;
                    resultString += loggedInUser.getFirstname() + " " + loggedInUser.getLastname();
                    resultString += MailgunUtil.REG_RECEIPT_FIVE;
                    long cartprice = 0;
                    long nondonation = 0;
                    long donation = 0;
                    long couponprice = 0;
                    for (CartItem ci : c.getCartItems()) {
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_A;
                    	if(ci.getEventCartItem().getType() == EventCartItemTypeEnum.T_SHIRT) {
                    		resultString += " T-Shirt - ";
                    	} else if(ci.getEventCartItem().getType() == EventCartItemTypeEnum.TICKET) {
                    		resultString += " Ticket - ";
                    	} else if(ci.getEventCartItem().getType() == EventCartItemTypeEnum.DONATION) {
                    		resultString += " Donation - ";
                    	}
                    	resultString += ci.getEventCartItem().getName();
                    	if(ci.getEventCartItem().getType() == EventCartItemTypeEnum.T_SHIRT) {
                    		resultString += " " + ci.getSize() + " " + ci.getColor();
                    	}
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_B;
                    	resultString += ci.getQuantity();
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_C;
                    	resultString += "$" + ci.getPrice();
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_D;
                    	cartprice += (ci.getPrice() * ci.getQuantity());
                    	if(ci.getEventCartItem().getType() != EventCartItemTypeEnum.DONATION) {
                    		nondonation += ci.getPrice() * ci.getQuantity();
                    	}
                    }
                    System.out.println("questions value: " + c.getQuestions());
                    if (c.getQuestions() != 0) {
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_A;
                    	resultString += " Survey Questions ";
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_B;
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_C;
                    	if(c.getQuestions() < 0) {
                    		resultString += "(";
                    	}
                    	resultString += "$" + c.getQuestions()/100 + ".";
                    	if(c.getQuestions() % 100 > 9) {
                        	resultString += c.getQuestions() % 100;
                        } else {
                        	resultString += "0" + c.getQuestions() % 10;
                        }
                    	if(c.getQuestions() < 0) {
                    		resultString += ")";
                    	}
                        resultString += MailgunUtil.REG_RECEIPT_SIX_D;
                    }
                    if (c.getCoupon() != null) {
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_A;
                    	resultString += " Coupon - " + c.getCoupon().getCode();
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_B;
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_C;
                    	donation = cartprice - nondonation;
                    	couponprice = c.getCoupon().getDiscount(nondonation*100);
                        resultString += "(" + "$" + couponprice/100 + ".";
                        if(couponprice % 100 > 9) {
                        	resultString += couponprice % 100;
                        } else {
                        	resultString += "0" + couponprice % 10;
                        }
                        resultString += ")";
                        resultString += MailgunUtil.REG_RECEIPT_SIX_D;
                    }
                    if (c.getReferralDiscount() > 0) {
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_A;
                    	resultString += " Sharing Discount" ;
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_B;
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_C;
                        resultString += "(" + "$" + c.getReferralDiscount()/100 + ".";
                        if(c.getReferralDiscount() % 100 > 9) {
                        	resultString += c.getReferralDiscount() % 100;
                        } else {
                        	resultString += "0" + c.getReferralDiscount() % 10;
                        }
                        resultString += ")";
                        resultString += MailgunUtil.REG_RECEIPT_SIX_D;
                    }
                    resultString += MailgunUtil.REG_RECEIPT_SIX_A;
                    resultString += "Bibs Fee";
                    resultString += MailgunUtil.REG_RECEIPT_SIX_B;
                    resultString += " ";
                    resultString += MailgunUtil.REG_RECEIPT_SIX_C;
                    long bibsfee = c.getTotal() - c.getTotalPreFee();
                    resultString += "$" + bibsfee/100 + ".";
                    if(bibsfee % 100 > 9) {
                    	resultString += bibsfee % 100;
                    } else {
                    	resultString += "0" + bibsfee % 10;
                    }
                    resultString += MailgunUtil.REG_RECEIPT_SIX_D;
                    resultString += MailgunUtil.REG_RECEIPT_SEVEN_A;
                    resultString += "$" + c.getTotal()/100 + ".";
                    if(c.getTotal() % 100 > 9) {
                    	resultString += c.getTotal() % 100;
                    } else {
                    	resultString += "0" + c.getTotal() % 10;
                    }
                    resultString += MailgunUtil.REG_RECEIPT_SEVEN_B;
                    resultString += MailgunUtil.REG_RECEIPT_EIGHT;
                    resultString += MailgunUtil.REG_RECEIPT_NINE_A;
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    timeFormat.setTimeZone(cartEvent.getTimezone());
                    dateFormat.setTimeZone(cartEvent.getTimezone());
                    resultString += timeFormat.format(cartEvent.getTimeStart());
                    resultString += "<br>";
                    resultString += dateFormat.format(cartEvent.getTimeStart());
                    resultString += MailgunUtil.REG_RECEIPT_NINE_B;
                    // Move Part ten to end of email
                    resultString += MailgunUtil.REG_RECEIPT_ELEVEN_A;
                    if(cartEvent.getAddress() != null) {
                        resultString += cartEvent.getAddress();
                        resultString += "<br>";
                    } else if(cartEvent.getLocation() != null) {
                    	resultString += cartEvent.getLocation();
                    	resultString += "<br>";
                    }
                    resultString += cartEvent.getCity();
                    if(cartEvent.getState() != null) {
                    	resultString += ", " + cartEvent.getState();
                    }
                    resultString += MailgunUtil.REG_RECEIPT_ELEVEN_B;
                    resultString += MailgunUtil.REG_RECEIPT_TWELVE_A;
                    resultString += loggedInUser.getEmergencyContactName() + " " + loggedInUser.getEmergencyContactPhone();
                    resultString += MailgunUtil.REG_RECEIPT_TWELVE_B;
                    resultString += MailgunUtil.REG_RECEIPT_THIRTEEN_A;
                    if(cartEvent.getWebsite() != null) {
                    	resultString += cartEvent.getWebsite() + "<br>";
                    }
                    if(cartEvent.getPhone() != null) {
                    	resultString += cartEvent.getPhone();
                    }
                    resultString += MailgunUtil.REG_RECEIPT_THIRTEEN_B;
                    resultString += MailgunUtil.REG_RECEIPT_TEN_A;
                    resultString += "https://overmind.bibs.io/bibs-server/rest/registrations/transfer?invoice=";
                    resultString += "B" + c.getId() + "T" + c.getStripeChargeId() + "&firstName=" + loggedInUser.getFirstname() + "&email=" + loggedInUser.getEmail();
                    resultString += MailgunUtil.REG_RECEIPT_TEN_B;
                    resultString += "B" + c.getId() + "T" + c.getStripeChargeId();
                    resultString += MailgunUtil.REG_RECEIPT_TEN_C;                    
                    resultString += MailgunUtil.REG_RECEIPT_FOURTEEN;
                    resultString += MailgunUtil.googleEventReservationCard(cartEvent, c.getId(), loggedInUser.getFirstname(), loggedInUser.getLastname());
                    /*
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
                    */
                    MailgunUtil.sendHTML(loggedInUser.getEmail(), "Thank you for registering with bibs!", resultString);
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

    public ResponseEntity<String> freeCartProcessing(Long cartId) {
        Cart c = Cart.findCart(cartId);
        if (c == null)
            return new ResponseEntity<>("cart not found", HttpStatus.NOT_FOUND);
        if (c.getStatus() != Cart.NEW && c.getStatus() != Cart.SAVED)
            return new ResponseEntity<>("cart already processed", HttpStatus.BAD_REQUEST);
        if (c.getTotal() > 0 ) {
        	return new ResponseEntity<>("NotFree", HttpStatus.BAD_REQUEST);
        }

        // in try block, so we can reset cart on failure
            // set cart to processing, so it doesn't get messed with (cart
            // timeout)
            c.setStatus(Cart.PROCESSING);
            c.persist();

            // HACK
            // TODO: this better
            long cartTotalCents = c.getTotal();
            // needs to be logged in, if
            // 1) no card token was submitted
            // 2) card should be remembered
            // failure if no card token provided and no customer saved
            UserProfile loggedInUser = UserProfileUtil.getLoggedInUserProfile();
            // HACK FOR CONFERENCE:
            if (loggedInUser != null) {
            	c.setUser(loggedInUser);
            	for(CartItem ci : c.getCartItems()) {
            		ci.setUserProfile(loggedInUser);
            		ci.persist();
            	}
            	c.persist();
            }
                c.setStatus(Cart.COMPLETE);
                c.persist();
                log.info("Completed charge for cart Id: " + c.getId() + " total: " + c.getTotal());
                //Review this plz
                for(CartItem ci : c.getCartItems()) {
                	EventCartItem eciUpdate = ci.getEventCartItem();
                	if (eciUpdate.getType() == EventCartItemTypeEnum.DONATION) {
                		eciUpdate.setPurchased(eciUpdate.getPurchased() + 1);
                	} else {
                    	eciUpdate.setPurchased(eciUpdate.getPurchased() + ci.getQuantity());
                	}
                	eciUpdate.merge();
                }

                // can only sent an email if we have a logged in users email
                if (loggedInUser != null) {
                    // this assumes that all cartitems belong to the same event,
                    // so only grabbing first one
                    Event cartEvent = c.getCartItems().get(0).getEventCartItem().getEvent();

                    // built text for order confirmation mail
                    String resultString = new String();
                    resultString = MailgunUtil.REG_RECEIPT_ONE;
                    resultString += cartEvent.getName();
                    resultString += MailgunUtil.REG_RECEIPT_TWO;
                    resultString += loggedInUser.getFirstname();
                    resultString += MailgunUtil.REG_RECEIPT_THREE;
                    resultString += cartEvent.getName();
                    resultString += MailgunUtil.REG_RECEIPT_FOUR;
                    resultString += loggedInUser.getFirstname() + " " + loggedInUser.getLastname();
                    resultString += MailgunUtil.REG_RECEIPT_FIVE;
                    long cartprice = 0;
                    long nondonation = 0;
                    long donation = 0;
                    long couponprice = 0;
                    for (CartItem ci : c.getCartItems()) {
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_A;
                    	if(ci.getEventCartItem().getType() == EventCartItemTypeEnum.T_SHIRT) {
                    		resultString += " T-Shirt - ";
                    	} else if(ci.getEventCartItem().getType() == EventCartItemTypeEnum.TICKET) {
                    		resultString += " Ticket - ";
                    	} else if(ci.getEventCartItem().getType() == EventCartItemTypeEnum.DONATION) {
                    		resultString += " Donation - ";
                    	}
                    	resultString += ci.getEventCartItem().getName();
                    	if(ci.getEventCartItem().getType() == EventCartItemTypeEnum.T_SHIRT) {
                    		resultString += " " + ci.getSize() + " " + ci.getColor();
                    	}
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_B;
                    	resultString += ci.getQuantity();
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_C;
                    	resultString += "$" + ci.getPrice();
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_D;
                    	cartprice += (ci.getPrice() * ci.getQuantity());
                    	if(ci.getEventCartItem().getType() != EventCartItemTypeEnum.DONATION) {
                    		nondonation += ci.getPrice() * ci.getQuantity();
                    	}
                    }
                    System.out.println("questions value: " + c.getQuestions());
                    if (c.getQuestions() > 0) {
                    	System.out.println("questions");
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_A;
                    	resultString += " Bells & whistles ";
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_B;
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_C;
                    	if(c.getQuestions() % 100 > 9) {
                        	resultString += c.getQuestions() % 100;
                        } else {
                        	resultString += "0" + c.getQuestions() % 10;
                        }
                        resultString += ")";
                        resultString += MailgunUtil.REG_RECEIPT_SIX_D;
                    }
                    if (c.getCoupon() != null) {
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_A;
                    	resultString += " Coupon - " + c.getCoupon().getCode();
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_B;
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_C;
                    	donation = cartprice - nondonation;
                    	couponprice = c.getCoupon().getDiscount(nondonation*100);
                        resultString += "(" + "$" + couponprice/100 + ".";
                        if(couponprice % 100 > 9) {
                        	resultString += couponprice % 100;
                        } else {
                        	resultString += "0" + couponprice % 10;
                        }
                        resultString += ")";
                        resultString += MailgunUtil.REG_RECEIPT_SIX_D;
                    }
                    if (c.getReferralDiscount() > 0) {
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_A;
                    	resultString += " Sharing Discount" ;
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_B;
                    	resultString += MailgunUtil.REG_RECEIPT_SIX_C;
                        resultString += "(" + "$" + c.getReferralDiscount()/100 + ".";
                        if(c.getReferralDiscount() % 100 > 9) {
                        	resultString += c.getReferralDiscount() % 100;
                        } else {
                        	resultString += "0" + c.getReferralDiscount() % 10;
                        }
                        resultString += ")";
                        resultString += MailgunUtil.REG_RECEIPT_SIX_D;
                    }
                    resultString += MailgunUtil.REG_RECEIPT_SIX_A;
                    resultString += "Bibs Fee";
                    resultString += MailgunUtil.REG_RECEIPT_SIX_B;
                    resultString += " ";
                    resultString += MailgunUtil.REG_RECEIPT_SIX_C;
                    long bibsfee = c.getTotal() - (cartprice * 100 - couponprice);
                    resultString += "$" + bibsfee/100 + ".";
                    if(bibsfee % 100 > 9) {
                    	resultString += bibsfee % 100;
                    } else {
                    	resultString += "0" + bibsfee % 10;
                    }
                    resultString += MailgunUtil.REG_RECEIPT_SIX_D;
                    resultString += MailgunUtil.REG_RECEIPT_SEVEN_A;
                    resultString += "$" + c.getTotal()/100 + ".";
                    if(c.getTotal() % 100 > 9) {
                    	resultString += c.getTotal() % 100;
                    } else {
                    	resultString += "0" + c.getTotal() % 10;
                    }
                    resultString += MailgunUtil.REG_RECEIPT_SEVEN_B;
                    resultString += MailgunUtil.REG_RECEIPT_EIGHT;
                    resultString += MailgunUtil.REG_RECEIPT_NINE_A;
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    timeFormat.setTimeZone(cartEvent.getTimezone());
                    dateFormat.setTimeZone(cartEvent.getTimezone());
                    resultString += timeFormat.format(cartEvent.getTimeStart());
                    resultString += "<br>";
                    resultString += dateFormat.format(cartEvent.getTimeStart());
                    resultString += MailgunUtil.REG_RECEIPT_NINE_B;
                    // Move Part ten to end of email
                    resultString += MailgunUtil.REG_RECEIPT_ELEVEN_A;
                    if(cartEvent.getAddress() != null) {
                        resultString += cartEvent.getAddress();
                        resultString += "<br>";
                    } else if(cartEvent.getLocation() != null) {
                    	resultString += cartEvent.getLocation();
                    	resultString += "<br>";
                    }
                    resultString += cartEvent.getCity();
                    if(cartEvent.getState() != null) {
                    	resultString += ", " + cartEvent.getState();
                    }
                    resultString += MailgunUtil.REG_RECEIPT_ELEVEN_B;
                    resultString += MailgunUtil.REG_RECEIPT_TWELVE_A;
                    resultString += loggedInUser.getEmergencyContactName() + " " + loggedInUser.getEmergencyContactPhone();
                    resultString += MailgunUtil.REG_RECEIPT_TWELVE_B;
                    resultString += MailgunUtil.REG_RECEIPT_THIRTEEN_A;
                    if(cartEvent.getWebsite() != null) {
                    	resultString += cartEvent.getWebsite() + "<br>";
                    }
                    if(cartEvent.getPhone() != null) {
                    	resultString += cartEvent.getPhone();
                    }
                    resultString += MailgunUtil.REG_RECEIPT_THIRTEEN_B;
                    resultString += MailgunUtil.REG_RECEIPT_TEN_A;
                    resultString += "https://overmind.bibs.io/bibs-server/rest/registrations/transfer?invoice=";
                    resultString += "B" + c.getId() + "T" + c.getStripeChargeId() + "&firstName=" + loggedInUser.getFirstname() + "&email=" + loggedInUser.getEmail();
                    resultString += MailgunUtil.REG_RECEIPT_TEN_B;
                    resultString += "B" + c.getId() + "T" + c.getStripeChargeId();
                    resultString += MailgunUtil.REG_RECEIPT_TEN_C;                    
                    resultString += MailgunUtil.REG_RECEIPT_FOURTEEN;
                    resultString += MailgunUtil.googleEventReservationCard(cartEvent, c.getId(), loggedInUser.getFirstname(), loggedInUser.getLastname());
                    MailgunUtil.sendHTML(loggedInUser.getEmail(), "Thank you for registering with bibs!", resultString);
                }

                return new ResponseEntity<>("FreeCartSuccess", HttpStatus.OK);
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
        log.info("Initiating refund for cart id: " + cart.getId());

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
            for(CartItem cartItem : cart.getCartItems()) {
            	EventCartItem eventCartItem = cartItem.getEventCartItem();
            	eventCartItem.setPurchased(eventCartItem.getPurchased() - cartItem.getQuantity());
            	eventCartItem.setAvailable(eventCartItem.getAvailable() + cartItem.getQuantity());
            	eventCartItem.persist();
            }
            if(cart.getUser() != null) {
            	MailgunUtil.send(cart.getUser().getEmail(), "Your order for " + cart.getEvent() + " was refunded", 
            			"Hey " + cart.getUser().getFirstname() + ",\nIt's too bad you couldn't make it to " + cart.getEvent().getName() +
            			". We refunded you for your order, and we hope to see you at the next one!\n-the bibs team"
            			);
            }
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
