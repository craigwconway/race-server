package com.bibsmobile;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
/*
@Configuration
public class WebappSpringConfig {

	@Autowired
	private Environment environment;
	
	@Bean(name = "springSecurityFilterChain")
	public FilterChainProxy springSecurityFilterChain() throws Exception {
		List<SecurityFilterChain> filterChains = new ArrayList<>();
		FilterChainProxy filterChainProxy = new FilterChainProxy(filterChains);
		return filterChainProxy;
	}
	
	
}
*/