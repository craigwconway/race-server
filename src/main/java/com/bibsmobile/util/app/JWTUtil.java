/**
 * 
 */
package com.bibsmobile.util.app;

import java.util.Date;
import java.util.HashMap;

import com.auth0.jwt.JWTSigner;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.UserProfileUtil;

/**
 * Utility for interacting with JWT for authentication.
 * @author galen
 *
 */
public class JWTUtil {
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
