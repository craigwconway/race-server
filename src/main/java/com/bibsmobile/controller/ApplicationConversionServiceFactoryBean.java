package com.bibsmobile.controller;

import com.bibsmobile.model.*;
import com.bibsmobile.service.UserProfileService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

@Configurable
/**
 * A central place to register application converters and formatters.
 */
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
        registry.addConverter(getEventToStringConverter());

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
    public Converter<UserGroup, String> getUserGroupToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserGroup, java.lang.String>() {
            public String convert(UserGroup userGroup) {
                return new StringBuilder().append(userGroup.getName()).append(' ').append(userGroup.getBibWrites()).toString();
            }
        };
    }
    
    public Converter<Long, UserGroup> getIdToUserGroupConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.UserGroup>() {
            public com.bibsmobile.model.UserGroup convert(java.lang.Long id) {
                return UserGroup.findUserGroup(id);
            }
        };
    }
    
    public Converter<String, UserGroup> getStringToUserGroupConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.UserGroup>() {
            public com.bibsmobile.model.UserGroup convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), UserGroup.class);
            }
        };
    }
    


	@Autowired
    UserProfileService userProfileService;

	public Converter<Cart, String> getCartToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.Cart, java.lang.String>() {
            public String convert(Cart cart) {
                return new StringBuilder().append(cart.getShipping()).append(' ').append(cart.getTotal()).append(' ').append(cart.getCreated()).append(' ').append(cart.getUpdated()).toString();
            }
        };
    }

	public Converter<Long, Cart> getIdToCartConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.Cart>() {
            public com.bibsmobile.model.Cart convert(java.lang.Long id) {
                return Cart.findCart(id);
            }
        };
    }

	public Converter<String, Cart> getStringToCartConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.Cart>() {
            public com.bibsmobile.model.Cart convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Cart.class);
            }
        };
    }

	public Converter<CartItem, String> getCartItemToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.CartItem, java.lang.String>() {
            public String convert(CartItem cartItem) {
                return new StringBuilder().append(cartItem.getQuantity()).append(' ').append(cartItem.getCreated()).append(' ').append(cartItem.getUpdated()).append(' ').append(cartItem.getComment()).toString();
            }
        };
    }

	public Converter<Long, CartItem> getIdToCartItemConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.CartItem>() {
            public com.bibsmobile.model.CartItem convert(java.lang.Long id) {
                return CartItem.findCartItem(id);
            }
        };
    }

	public Converter<String, CartItem> getStringToCartItemConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.CartItem>() {
            public com.bibsmobile.model.CartItem convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), CartItem.class);
            }
        };
    }

	public Converter<EventAlert, String> getEventAlertToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventAlert, java.lang.String>() {
            public String convert(EventAlert eventAlert) {
                return new StringBuilder().append(eventAlert.getText()).toString();
            }
        };
    }

	public Converter<Long, EventAlert> getIdToEventAlertConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventAlert>() {
            public com.bibsmobile.model.EventAlert convert(java.lang.Long id) {
                return EventAlert.findEventAlert(id);
            }
        };
    }

	public Converter<String, EventAlert> getStringToEventAlertConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventAlert>() {
            public com.bibsmobile.model.EventAlert convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventAlert.class);
            }
        };
    }

	public Converter<EventCartItem, String> getEventCartItemToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventCartItem, java.lang.String>() {
            public String convert(EventCartItem eventCartItem) {
                return new StringBuilder().append(eventCartItem.getName()).append(' ').append(eventCartItem.getDescription()).append(' ').append(eventCartItem.getPrice()).append(' ').append(eventCartItem.getAvailable()).toString();
            }
        };
    }

	public Converter<Long, EventCartItem> getIdToEventCartItemConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventCartItem>() {
            public com.bibsmobile.model.EventCartItem convert(java.lang.Long id) {
                return EventCartItem.findEventCartItem(id);
            }
        };
    }

	public Converter<String, EventCartItem> getStringToEventCartItemConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventCartItem>() {
            public com.bibsmobile.model.EventCartItem convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventCartItem.class);
            }
        };
    }

	public Converter<EventCartItemPriceChange, String> getEventCartItemPriceChangeToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventCartItemPriceChange, java.lang.String>() {
            public String convert(EventCartItemPriceChange eventCartItemPriceChange) {
                return new StringBuilder().append(eventCartItemPriceChange.getStartDate()).append(' ').append(eventCartItemPriceChange.getEndDate()).append(' ').append(eventCartItemPriceChange.getPrice()).toString();
            }
        };
    }

	public Converter<Long, EventCartItemPriceChange> getIdToEventCartItemPriceChangeConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventCartItemPriceChange>() {
            public com.bibsmobile.model.EventCartItemPriceChange convert(java.lang.Long id) {
                return EventCartItemPriceChange.findEventCartItemPriceChange(id);
            }
        };
    }

	public Converter<String, EventCartItemPriceChange> getStringToEventCartItemPriceChangeConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventCartItemPriceChange>() {
            public com.bibsmobile.model.EventCartItemPriceChange convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventCartItemPriceChange.class);
            }
        };
    }

	public Converter<EventMap, String> getEventMapToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventMap, java.lang.String>() {
            public String convert(EventMap eventMap) {
                return new StringBuilder().append(eventMap.getUrl()).toString();
            }
        };
    }

	public Converter<Long, EventMap> getIdToEventMapConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventMap>() {
            public com.bibsmobile.model.EventMap convert(java.lang.Long id) {
                return EventMap.findEventMap(id);
            }
        };
    }

	public Converter<String, EventMap> getStringToEventMapConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventMap>() {
            public com.bibsmobile.model.EventMap convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventMap.class);
            }
        };
    }

	public Converter<EventPhoto, String> getEventPhotoToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventPhoto, java.lang.String>() {
            public String convert(EventPhoto eventPhoto) {
                return new StringBuilder().append(eventPhoto.getUrl()).toString();
            }
        };
    }

	public Converter<Long, EventPhoto> getIdToEventPhotoConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventPhoto>() {
            public com.bibsmobile.model.EventPhoto convert(java.lang.Long id) {
                return EventPhoto.findEventPhoto(id);
            }
        };
    }

	public Converter<String, EventPhoto> getStringToEventPhotoConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventPhoto>() {
            public com.bibsmobile.model.EventPhoto convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventPhoto.class);
            }
        };
    }

	public Converter<EventResult, String> getEventResultToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventResult, java.lang.String>() {
            public String convert(EventResult eventResult) {
                return new StringBuilder().append(eventResult.getText()).toString();
            }
        };
    }

	public Converter<Long, EventResult> getIdToEventResultConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventResult>() {
            public com.bibsmobile.model.EventResult convert(java.lang.Long id) {
                return EventResult.findEventResult(id);
            }
        };
    }

	public Converter<String, EventResult> getStringToEventResultConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventResult>() {
            public com.bibsmobile.model.EventResult convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventResult.class);
            }
        };
    }

	public Converter<EventType, String> getEventTypeToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventType, java.lang.String>() {
            public String convert(EventType eventType) {
                return new StringBuilder().append(eventType.getTypeName()).append(' ').append(eventType.getStartTime()).toString();
            }
        };
    }

	public Converter<Long, EventType> getIdToEventTypeConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventType>() {
            public com.bibsmobile.model.EventType convert(java.lang.Long id) {
                return EventType.findEventType(id);
            }
        };
    }

	public Converter<String, EventType> getStringToEventTypeConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventType>() {
            public com.bibsmobile.model.EventType convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventType.class);
            }
        };
    }

	public Converter<EventUserGroup, String> getEventUserGroupToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventUserGroup, java.lang.String>() {
            public String convert(EventUserGroup eventUserGroup) {
                return "(no displayable fields)";
            }
        };
    }

	public Converter<EventUserGroupId, EventUserGroup> getIdToEventUserGroupConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventUserGroupId, com.bibsmobile.model.EventUserGroup>() {
            public com.bibsmobile.model.EventUserGroup convert(com.bibsmobile.model.EventUserGroupId id) {
                return EventUserGroup.findEventUserGroup(id);
            }
        };
    }

	public Converter<String, EventUserGroup> getStringToEventUserGroupConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventUserGroup>() {
            public com.bibsmobile.model.EventUserGroup convert(String id) {
                return getObject().convert(getObject().convert(id, EventUserGroupId.class), EventUserGroup.class);
            }
        };
    }

	public Converter<RaceImage, String> getRaceImageToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.RaceImage, java.lang.String>() {
            public String convert(RaceImage raceImage) {
                return new StringBuilder().append(raceImage.getFilePath()).toString();
            }
        };
    }

	public Converter<Long, RaceImage> getIdToRaceImageConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.RaceImage>() {
            public com.bibsmobile.model.RaceImage convert(java.lang.Long id) {
                return RaceImage.findRaceImage(id);
            }
        };
    }

	public Converter<String, RaceImage> getStringToRaceImageConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.RaceImage>() {
            public com.bibsmobile.model.RaceImage convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), RaceImage.class);
            }
        };
    }

	public Converter<RaceResult, String> getRaceResultToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.RaceResult, java.lang.String>() {
            public String convert(RaceResult raceResult) {
                return new StringBuilder().append(raceResult.getTimeofficialdisplay()).append(' ').append(raceResult.getBib()).append(' ').append(raceResult.getFirstname()).append(' ').append(raceResult.getLastname()).toString();
            }
        };
    }

	public Converter<Long, RaceResult> getIdToRaceResultConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.RaceResult>() {
            public com.bibsmobile.model.RaceResult convert(java.lang.Long id) {
                return RaceResult.findRaceResult(id);
            }
        };
    }

	public Converter<String, RaceResult> getStringToRaceResultConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.RaceResult>() {
            public com.bibsmobile.model.RaceResult convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), RaceResult.class);
            }
        };
    }

	public Converter<ResultsFile, String> getResultsFileToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.ResultsFile, java.lang.String>() {
            public String convert(ResultsFile resultsFile) {
                return new StringBuilder().append(resultsFile.getName()).append(' ').append(resultsFile.getContentType()).append(' ').append(resultsFile.getCreated()).append(' ').append(resultsFile.getFilesize()).toString();
            }
        };
    }

	public Converter<Long, ResultsFile> getIdToResultsFileConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.ResultsFile>() {
            public com.bibsmobile.model.ResultsFile convert(java.lang.Long id) {
                return ResultsFile.findResultsFile(id);
            }
        };
    }

	public Converter<String, ResultsFile> getStringToResultsFileConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.ResultsFile>() {
            public com.bibsmobile.model.ResultsFile convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), ResultsFile.class);
            }
        };
    }

	public Converter<ResultsFileMapping, String> getResultsFileMappingToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.ResultsFileMapping, java.lang.String>() {
            public String convert(ResultsFileMapping resultsFileMapping) {
                return new StringBuilder().append(resultsFileMapping.getName()).append(' ').append(resultsFileMapping.getMap()).toString();
            }
        };
    }

	public Converter<Long, ResultsFileMapping> getIdToResultsFileMappingConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.ResultsFileMapping>() {
            public com.bibsmobile.model.ResultsFileMapping convert(java.lang.Long id) {
                return ResultsFileMapping.findResultsFileMapping(id);
            }
        };
    }

	public Converter<String, ResultsFileMapping> getStringToResultsFileMappingConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.ResultsFileMapping>() {
            public com.bibsmobile.model.ResultsFileMapping convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), ResultsFileMapping.class);
            }
        };
    }

	public Converter<ResultsImport, String> getResultsImportToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.ResultsImport, java.lang.String>() {
            public String convert(ResultsImport resultsImport) {
                return new StringBuilder().append(resultsImport.getRunDate()).append(' ').append(resultsImport.getRowsProcessed()).append(' ').append(resultsImport.getErrors()).append(' ').append(resultsImport.getErrorRows()).toString();
            }
        };
    }

	public Converter<Long, ResultsImport> getIdToResultsImportConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.ResultsImport>() {
            public com.bibsmobile.model.ResultsImport convert(java.lang.Long id) {
                return ResultsImport.findResultsImport(id);
            }
        };
    }

	public Converter<String, ResultsImport> getStringToResultsImportConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.ResultsImport>() {
            public com.bibsmobile.model.ResultsImport convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), ResultsImport.class);
            }
        };
    }

	public Converter<TimerConfig, String> getTimerConfigToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.TimerConfig, java.lang.String>() {
            public String convert(TimerConfig timerConfig) {
                return new StringBuilder().append(timerConfig.getConnectionTimeout()).append(' ').append(timerConfig.getFilename()).append(' ').append(timerConfig.getPosition()).append(' ').append(timerConfig.getUrl()).toString();
            }
        };
    }

	public Converter<Long, TimerConfig> getIdToTimerConfigConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.TimerConfig>() {
            public com.bibsmobile.model.TimerConfig convert(java.lang.Long id) {
                return TimerConfig.findTimerConfig(id);
            }
        };
    }

	public Converter<String, TimerConfig> getStringToTimerConfigConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.TimerConfig>() {
            public com.bibsmobile.model.TimerConfig convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), TimerConfig.class);
            }
        };
    }

	public Converter<Long, UserAuthority> getIdToUserAuthorityConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.UserAuthority>() {
            public com.bibsmobile.model.UserAuthority convert(java.lang.Long id) {
                return UserAuthority.findUserAuthority(id);
            }
        };
    }

	public Converter<String, UserAuthority> getStringToUserAuthorityConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.UserAuthority>() {
            public com.bibsmobile.model.UserAuthority convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), UserAuthority.class);
            }
        };
    }

	public Converter<UserGroupUserAuthority, String> getUserGroupUserAuthorityToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserGroupUserAuthority, java.lang.String>() {
            public String convert(UserGroupUserAuthority userGroupUserAuthority) {
                return "(no displayable fields)";
            }
        };
    }

	public Converter<UserGroupUserAuthorityID, UserGroupUserAuthority> getIdToUserGroupUserAuthorityConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserGroupUserAuthorityID, com.bibsmobile.model.UserGroupUserAuthority>() {
            public com.bibsmobile.model.UserGroupUserAuthority convert(com.bibsmobile.model.UserGroupUserAuthorityID id) {
                return UserGroupUserAuthority.findUserGroupUserAuthority(id);
            }
        };
    }

	public Converter<String, UserGroupUserAuthority> getStringToUserGroupUserAuthorityConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.UserGroupUserAuthority>() {
            public com.bibsmobile.model.UserGroupUserAuthority convert(String id) {
                return getObject().convert(getObject().convert(id, UserGroupUserAuthorityID.class), UserGroupUserAuthority.class);
            }
        };
    }

	public Converter<Long, UserProfile> getIdToUserProfileConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.UserProfile>() {
            public com.bibsmobile.model.UserProfile convert(java.lang.Long id) {
                return userProfileService.findUserProfile(id);
            }
        };
    }

	public Converter<String, UserProfile> getStringToUserProfileConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.UserProfile>() {
            public com.bibsmobile.model.UserProfile convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), UserProfile.class);
            }
        };
    }

	public Converter<String, UserGroupUserAuthorityID> getJsonToUserGroupUserAuthorityIDConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.UserGroupUserAuthorityID>() {
            public UserGroupUserAuthorityID convert(String encodedJson) {
                return UserGroupUserAuthorityID.fromJsonToUserGroupUserAuthorityID(new String(Base64.decodeBase64(encodedJson)));
            }
        };
    }

	public Converter<UserGroupUserAuthorityID, String> getUserGroupUserAuthorityIDToJsonConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserGroupUserAuthorityID, java.lang.String>() {
            public String convert(UserGroupUserAuthorityID userGroupUserAuthorityID) {
                return Base64.encodeBase64URLSafeString(userGroupUserAuthorityID.toJson().getBytes());
            }
        };
    }

	public Converter<String, EventUserGroupId> getJsonToEventUserGroupIdConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventUserGroupId>() {
            public EventUserGroupId convert(String encodedJson) {
                return EventUserGroupId.fromJsonToEventUserGroupId(new String(Base64.decodeBase64(encodedJson)));
            }
        };
    }

	public Converter<EventUserGroupId, String> getEventUserGroupIdToJsonConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventUserGroupId, java.lang.String>() {
            public String convert(EventUserGroupId eventUserGroupId) {
                return Base64.encodeBase64URLSafeString(eventUserGroupId.toJson().getBytes());
            }
        };
    }

	public void installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getCartToStringConverter());
        registry.addConverter(getIdToCartConverter());
        registry.addConverter(getStringToCartConverter());
        registry.addConverter(getCartItemToStringConverter());
        registry.addConverter(getIdToCartItemConverter());
        registry.addConverter(getStringToCartItemConverter());
        registry.addConverter(getEventAlertToStringConverter());
        registry.addConverter(getIdToEventAlertConverter());
        registry.addConverter(getStringToEventAlertConverter());
        registry.addConverter(getEventCartItemToStringConverter());
        registry.addConverter(getIdToEventCartItemConverter());
        registry.addConverter(getStringToEventCartItemConverter());
        registry.addConverter(getEventCartItemPriceChangeToStringConverter());
        registry.addConverter(getIdToEventCartItemPriceChangeConverter());
        registry.addConverter(getStringToEventCartItemPriceChangeConverter());
        registry.addConverter(getEventMapToStringConverter());
        registry.addConverter(getIdToEventMapConverter());
        registry.addConverter(getStringToEventMapConverter());
        registry.addConverter(getEventPhotoToStringConverter());
        registry.addConverter(getIdToEventPhotoConverter());
        registry.addConverter(getStringToEventPhotoConverter());
        registry.addConverter(getEventResultToStringConverter());
        registry.addConverter(getIdToEventResultConverter());
        registry.addConverter(getStringToEventResultConverter());
        registry.addConverter(getEventTypeToStringConverter());
        registry.addConverter(getIdToEventTypeConverter());
        registry.addConverter(getStringToEventTypeConverter());
        registry.addConverter(getEventUserGroupToStringConverter());
        registry.addConverter(getIdToEventUserGroupConverter());
        registry.addConverter(getStringToEventUserGroupConverter());
        registry.addConverter(getRaceImageToStringConverter());
        registry.addConverter(getIdToRaceImageConverter());
        registry.addConverter(getStringToRaceImageConverter());
        registry.addConverter(getRaceResultToStringConverter());
        registry.addConverter(getIdToRaceResultConverter());
        registry.addConverter(getStringToRaceResultConverter());
        registry.addConverter(getResultsFileToStringConverter());
        registry.addConverter(getIdToResultsFileConverter());
        registry.addConverter(getStringToResultsFileConverter());
        registry.addConverter(getResultsFileMappingToStringConverter());
        registry.addConverter(getIdToResultsFileMappingConverter());
        registry.addConverter(getStringToResultsFileMappingConverter());
        registry.addConverter(getResultsImportToStringConverter());
        registry.addConverter(getIdToResultsImportConverter());
        registry.addConverter(getStringToResultsImportConverter());
        registry.addConverter(getTimerConfigToStringConverter());
        registry.addConverter(getIdToTimerConfigConverter());
        registry.addConverter(getStringToTimerConfigConverter());
        registry.addConverter(getUserAuthoritiesToStringConverter());
        registry.addConverter(getIdToUserAuthoritiesConverter());
        registry.addConverter(getStringToUserAuthoritiesConverter());
        registry.addConverter(getUserAuthorityToStringConverter());
        registry.addConverter(getIdToUserAuthorityConverter());
        registry.addConverter(getStringToUserAuthorityConverter());
        registry.addConverter(getUserGroupToStringConverter());
        registry.addConverter(getIdToUserGroupConverter());
        registry.addConverter(getStringToUserGroupConverter());
        registry.addConverter(getUserGroupUserAuthorityToStringConverter());
        registry.addConverter(getIdToUserGroupUserAuthorityConverter());
        registry.addConverter(getStringToUserGroupUserAuthorityConverter());
        registry.addConverter(getUserProfileToStringConverter());
        registry.addConverter(getIdToUserProfileConverter());
        registry.addConverter(getStringToUserProfileConverter());
        registry.addConverter(getJsonToUserGroupUserAuthorityIDConverter());
        registry.addConverter(getUserGroupUserAuthorityIDToJsonConverter());
        registry.addConverter(getJsonToEventUserGroupIdConverter());
        registry.addConverter(getEventUserGroupIdToJsonConverter());
    }

	public void afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
}
