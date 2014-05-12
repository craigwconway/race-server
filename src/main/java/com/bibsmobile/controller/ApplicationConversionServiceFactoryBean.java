package com.bibsmobile.controller;

import com.bibsmobile.model.*;
import org.apache.commons.codec.binary.Base64;
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
        registry.addConverter(getJsonToUserAuthoritiesIDConverter());
        registry.addConverter(getUserAuthoritiesIDToJsonConverter());
        registry.addConverter(getUserAuthoritiesToStringConverter());
        registry.addConverter(getIdToUserAuthoritiesConverter());
        registry.addConverter(getStringToUserAuthoritiesConverter());
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


    public Converter<String, UserAuthoritiesID> getJsonToUserAuthoritiesIDConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.UserAuthoritiesID>() {
            public UserAuthoritiesID convert(String encodedJson) {
                return UserAuthoritiesID.fromJsonToUserAuthoritiesID(new String(Base64.decodeBase64(encodedJson)));
            }
        };
    }

    public Converter<UserAuthoritiesID, String> getUserAuthoritiesIDToJsonConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserAuthoritiesID, java.lang.String>() {
            public String convert(UserAuthoritiesID userAuthoritiesID) {
                return Base64.encodeBase64URLSafeString(userAuthoritiesID.toJson().getBytes());
            }
        };
    }

    public Converter<UserAuthorities, String> getUserAuthoritiesToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserAuthorities, java.lang.String>() {
            public String convert(UserAuthorities userAuthorities) {
                return "(no displayable fields)";
            }
        };
    }

    public Converter<UserAuthoritiesID, UserAuthorities> getIdToUserAuthoritiesConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserAuthoritiesID, com.bibsmobile.model.UserAuthorities>() {
            public com.bibsmobile.model.UserAuthorities convert(com.bibsmobile.model.UserAuthoritiesID id) {
                return UserAuthorities.findUserAuthorities(id);
            }
        };
    }

    public Converter<String, UserAuthorities> getStringToUserAuthoritiesConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.UserAuthorities>() {
            public com.bibsmobile.model.UserAuthorities convert(String id) {
                return getObject().convert(getObject().convert(id, UserAuthoritiesID.class), UserAuthorities.class);
            }
        };
    }


}
