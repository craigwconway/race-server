/**
 * 
 */
package com.bibsmobile.util.app;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.bibsmobile.filter.JWTAuthenticationToken;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.UserProfileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility for interacting with JWT for authentication.
 * @author galen
 *
 */
public class JWTUtil {
	
	private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationToken.class);
	
	public static UserProfile authenticate(String jwt) {
		JWTVerifier verifier = new JWTVerifier("doge");
		Map <String, Object> explodedToken = new HashMap <String, Object> ();
        try {
			explodedToken = verifier.verify(jwt);
			System.out.println(explodedToken.get("user"));
			ObjectMapper mapper = new ObjectMapper();
			return mapper.convertValue(explodedToken.get("user"), AuthUser.class).toUserProfile();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			log.error("Invalid Algorithm " + e);
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			log.error("Error parsing token - invalid state " + e);
			e.printStackTrace();
			return null;
		} catch (SignatureException e) {
			log.warn("Signature exception in incoming token " + e);
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			log.error("IO exception in incoming token " + e );
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (JWTVerifyException e) {
			log.info("invalid token");
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static String generate(UserProfile user) {
		JWTSigner signer = new JWTSigner("doge");
		HashMap<String, Object> claims = new HashMap<String, Object>();
		AuthUser authUser = new AuthUser(user);
		claims.put("user", authUser);
		claims.put("iss", "bibs");
		claims.put("aud", user.getId().toString());
		claims.put("iat", new Date().getTime());
		claims.put("exp", new Date().getTime() + 1000 * 60 * 60 * 24 * 30 );
		//signer.sign()
		return signer.sign(claims);
	}
	
    private static class AuthUser {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String username;
        private String password;

        protected AuthUser() {
            super();
        }
        
        protected UserProfile toUserProfile() {
        	UserProfile user = new UserProfile();
        	user.setId(id);
        	user.setFirstname(firstName);
        	user.setLastname(lastName);
        	user.setEmail(email);
        	user.setPhone(phone);
        	user.setUsername(username);
        	user.setPassword(password);
        	return user;
        }

        protected AuthUser(UserProfile u) {
            super();
            this.id = u.getId();
            this.firstName = u.getFirstname();
            this.lastName = u.getLastname();
            this.email = u.getEmail();
            this.phone = u.getPhone();
            this.username = u.getUsername();
            this.password = u.getPassword();
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

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
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

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }	
	
}
