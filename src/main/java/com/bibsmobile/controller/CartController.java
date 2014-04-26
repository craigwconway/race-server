package com.bibsmobile.controller;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.authorize.sim.Fingerprint;

import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.UserProfile;

@RequestMapping("/carts")
@Controller
@RooWebScaffold(path = "carts", formBackingObject = Cart.class)
@RooWebJson(jsonObject = Cart.class)
public class CartController { 
    
    @RequestMapping(value = "/item/{id}", produces = "text/html")
    public String addItem(@PathVariable("id") Long eventCartItemId, Model uiModel) {
    	String username = SecurityContextHolder.getContext()
				.getAuthentication().getName();
		UserProfile user = UserProfile.findUserProfilesByUsernameEquals(
				username).getSingleResult();
		Cart cart = new Cart();
		try{
			List<Cart> carts = Cart.findCartsByUser(user).getResultList();
			for(Cart c : carts){
				if(c.getStatus()==Cart.NEW){
					cart = c;
				}
			}
		}catch(Exception e){ System.out.println("ERROR ADDING TO CART");}
    	
		Date now = new Date();
		
		if(cart.getId()==null){
    		cart = new Cart();
    		cart.setStatus(Cart.NEW);
    		
    		cart.setCreated(now);
    		cart.setUpdated(now);
    		cart.setUser(user);
    		cart.persist();
    	}
		
		List<CartItem> items = (cart.getCartItems()!=null)?cart.getCartItems():new ArrayList<CartItem>();
    	EventCartItem i = EventCartItem.findEventCartItem(eventCartItemId);
		CartItem item = new CartItem();
		item.setCart(cart);
		item.setEventCartItem(i);
		item.setQuantity(1);
		item.setCreated(now);
		item.setUpdated(now);
		item.persist();
		
		items.add(item);
		cart.setCartItems(items);
		double total = 0;
		for(CartItem cartItem : items){
			total += (cartItem.getQuantity() * cartItem.getEventCartItem().getPrice());
		}
		cart.setTotal(total);
		cart.merge();
		
		setupPaymentForm(uiModel, cart, true);
    	
    	uiModel.addAttribute("cart", cart);
        return "cart";
    }

	private void setupPaymentForm(Model uiModel, Cart cart, boolean test) {
		
		   String apiLoginId = "7rWKZe476";
		    uiModel.addAttribute("apiLoginId", apiLoginId); 
		    
		    String transactionKey = (test)?"5Fg6846nb7pAS4X4":""+cart.getId();
		    System.out.println(transactionKey+" transactionKey");
		    uiModel.addAttribute("transactionKey", transactionKey); 
		    
		    String relayResponseUrl = "http:/localhost:8080/bibs-server/events/registrationComplete?event=1";
		    uiModel.addAttribute("relayResponseUrl", relayResponseUrl); 

		    double amount = (test)?new Random().nextDouble()+.01:cart.getTotal();
		    
		    NumberFormat df = DecimalFormat.getInstance();
		    df.setMaximumFractionDigits(2);
		    String samount = df.format(amount);
		    uiModel.addAttribute("amount", samount);  
		    
		    Fingerprint fingerprint = Fingerprint.createFingerprint(
		        apiLoginId,
		        transactionKey,
		        1234567890,  // random sequence used for creating the finger print
		        samount);

		    long x_fp_sequence = fingerprint.getSequence();
		    uiModel.addAttribute("x_fp_sequence", x_fp_sequence); 
		    
		    long x_fp_timestamp = fingerprint.getTimeStamp();
		    uiModel.addAttribute("x_fp_timestamp", x_fp_timestamp); 
		    
		    String x_fp_hash = fingerprint.getFingerprintHash();
		    uiModel.addAttribute("x_fp_hash", x_fp_hash); 
		
	}
}