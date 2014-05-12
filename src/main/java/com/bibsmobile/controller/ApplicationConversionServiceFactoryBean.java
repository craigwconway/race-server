package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserProfile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.converter.RooConversionService;

/**
 * A central place to register application converters and formatters.
 */
@RooConversionService
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

    @Override
    protected void installFormatters(FormatterRegistry registry) {
        super.installFormatters(registry);
        // Register application converters and formatters
        registry.addConverter(getUserAuthoritiesIDToStringConverter());
    }

    public Converter<Event, String> getEventToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.Event, java.lang.String>() {
            public String convert(Event event) {
                return event.getName();
            }
        };
    }

    public Converter<UserProfile, String> getUserProfileToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserProfile, java.lang.String>() {
            public String convert(UserProfile userProfile) {
                return userProfile.getUsername();
            }
        };
    }

    public Converter<UserAuthority, String> getUserAuthorityToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserAuthority, java.lang.String>() {
            public String convert(UserAuthority userAuthority) {
                String name = "";
                if (userAuthority.getAuthority().equals("ROLE_SYS_ADMIN")) {
                    name = "System Admin";
                } else if (userAuthority.getAuthority().equals("ROLE_EVENT_ADMIN")) {
                    name = "Event Admin";
                } else if (userAuthority.getAuthority().equals("ROLE_USER_ADMIN")) {
                    name = "Registration Admin";
                } else if (userAuthority.getAuthority().equals("ROLE_USER")) {
                    name = "Registered Runner";
                }
                return name;
            }
        };
    }

    public Converter<UserAuthoritiesID, String> getUserAuthoritiesIDToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserAuthoritiesID, java.lang.String>() {
            public String convert(UserAuthoritiesID userAuthoritiesID) {

                return new StringBuilder(userAuthoritiesID.getUserProfile().getUsername()).append(' ').append(userAuthoritiesID.getUserAuthority().getAuthority().toString()).toString();
            }
        };
    }

}
