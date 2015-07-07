package com.bibsmobile.filter;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.bibsmobile.model.UserProfile;

/**
 * JWTAuthenticationToken is a Java Spring implementation of the Json Web Token format.
 * It has a .io domain (jwt.io), so we know it's legit.
 * @author galen
 *
 */
public class JWTAuthenticationToken extends AbstractAuthenticationToken{
	
	private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationToken.class);
	
    private static final long serialVersionUID = 1L;
    private final Object principal;
    private UserProfile details;
 
    Collection  authorities;
    public JWTAuthenticationToken( String jwtToken) {
        super(null);
        super.setAuthenticated(true); // must use super, as we override
        //JWTParser parser = new JWTParser(jwtToken);
        //this.principal=parser.getSub();
        //JWTVerifier verifier = new JWTVerifier();
        JWTVerifier verifier = new JWTVerifier("doge");
        Map <String, Object> explodedToken = new HashMap <String, Object>();
        System.out.println("Entered JWTAuthenticationToken constructor\n-----Input-----\n" + jwtToken +"\n-----End-----");
        try {
			verifier.verify(jwtToken);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			log.error("Invalid Algorithm " + e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			log.error("Error parsing token - invalid state " + e);
			e.printStackTrace();
		} catch (SignatureException e) {
			log.warn("Signature exception in incoming token " + e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			log.error("IO exception in incoming token " + e );
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JWTVerifyException e) {
			log.info("invalid token");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.principal = null;
        System.out.println(explodedToken);
        
        //this.setDetailsAuthorities();

        
    }
 
    @Override
    public Object getCredentials() {
        return "";
    }
 
    @Override
    public Object getPrincipal() {
        return principal;
    }
    private void setDetailsAuthorities() {
        String username = principal.toString();
        UserProfile user = UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
        details=user;
        authorities= user.getAuthorities();
    }
 
    @Override
    public Collection getAuthorities() {
        return authorities;
    }
}
