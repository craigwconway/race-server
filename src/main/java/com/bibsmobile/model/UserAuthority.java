package com.bibsmobile.model;

import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@SuppressWarnings({ "serial" })
@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class UserAuthority implements  org.springframework.security.core.GrantedAuthority {

	@NotNull
	private String authority;

	@Override
	public String getAuthority() {
		return authority;
	}

}
