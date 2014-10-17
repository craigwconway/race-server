package com.bibsmobile.rest.security;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.bibsmobile.model.UserProfile;

import flexjson.JSONSerializer;

@Component(value = "restAuthenticationSuccessHandler")
public class RestLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final String sessionId = request.getSession().getId();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        final UserProfile user = UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
        response.getWriter().write(new JSONSerializer().exclude("*.class").include("userprofile.userAuthorities.userGroupUserAuthorities").serialize(new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("jsessionid", sessionId);
                put("userprofile", user);
            }
        }));

        clearAuthenticationAttributes(request);
    }

}