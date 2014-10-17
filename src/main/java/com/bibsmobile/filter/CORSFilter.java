package com.bibsmobile.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Jevgeni on 9.05.2014.
 */
@Component
public class CORSFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Expose-Headers", "Content-Length, Content-Type, Date, JSESSIONID");
        response.setHeader("JSESSIONID", request.getSession(true).getId());
        if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE");
            response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Origin,Content-Type, Accept");
        }
        filterChain.doFilter(request, response);
    }

}
