/**
 * 
 */
package com.bibsmobile.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Handle entries to app controller
 * @author galen
 *
 */
@Component
public class AppAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	private static final Logger log = LoggerFactory.getLogger(CustomTokenAuthenticationFilter.class);
	
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException ) throws IOException, ServletException {
        String contentType = request.getContentType();
        log.info(contentType);
        response.sendError( HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" );
    }
 
}
