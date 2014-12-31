package com.bibsmobile.controller;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventAlert;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemPriceChange;
import com.bibsmobile.model.EventMap;
import com.bibsmobile.model.EventPhoto;
import com.bibsmobile.model.EventResult;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.EventUserGroupId;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.ResultsFileMapping;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.model.TimerConfig;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserGroupUserAuthorityID;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.UserProfileService;

@Configurable
/**
 * A central place to register application converters and formatters.
 */
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

    @Override
    protected void installFormatters(FormatterRegistry registry) {
        super.installFormatters(registry);
        // Register application converters and formatters
        registry.addConverter(this.getJsonToUserAuthoritiesIDConverter());
        registry.addConverter(this.getUserAuthoritiesIDToJsonConverter());
        registry.addConverter(this.getUserAuthoritiesToStringConverter());
        registry.addConverter(this.getIdToUserAuthoritiesConverter());
        registry.addConverter(this.getStringToUserAuthoritiesConverter());
        registry.addConverter(this.getEventToStringConverter());

    }

    public Converter<Event, String> getEventToStringConverter() {
        return new Converter<Event, String>() {
            @Override
            public String convert(Event event) {
                return event.getName();
            }
        };
    }

    public Converter<UserProfile, String> getUserProfileToStringConverter() {
        return new Converter<UserProfile, String>() {
            @Override
            public String convert(UserProfile userProfile) {
                return userProfile.getUsername();
            }
        };
    }

    public Converter<UserAuthority, String> getUserAuthorityToStringConverter() {
        return new Converter<UserAuthority, String>() {
            @Override
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
        return new Converter<String, UserAuthoritiesID>() {
            @Override
            public UserAuthoritiesID convert(String encodedJson) {
                return UserAuthoritiesID.fromJsonToUserAuthoritiesID(new String(Base64.decodeBase64(encodedJson)));
            }
        };
    }

    public Converter<UserAuthoritiesID, String> getUserAuthoritiesIDToJsonConverter() {
        return new Converter<UserAuthoritiesID, String>() {
            @Override
            public String convert(UserAuthoritiesID userAuthoritiesID) {
                return Base64.encodeBase64URLSafeString(userAuthoritiesID.toJson().getBytes());
            }
        };
    }

    public Converter<UserAuthorities, String> getUserAuthoritiesToStringConverter() {
        return new Converter<UserAuthorities, String>() {
            @Override
            public String convert(UserAuthorities userAuthorities) {
                return "(no displayable fields)";
            }
        };
    }

    public Converter<UserAuthoritiesID, UserAuthorities> getIdToUserAuthoritiesConverter() {
        return new Converter<UserAuthoritiesID, UserAuthorities>() {
            @Override
            public UserAuthorities convert(UserAuthoritiesID id) {
                return UserAuthorities.findUserAuthorities(id);
            }
        };
    }

    public Converter<String, UserAuthorities> getStringToUserAuthoritiesConverter() {
        return new Converter<String, UserAuthorities>() {
            @Override
            public UserAuthorities convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, UserAuthoritiesID.class), UserAuthorities.class);
            }
        };
    }

    public Converter<UserGroup, String> getUserGroupToStringConverter() {
        return new Converter<UserGroup, String>() {
            @Override
            public String convert(UserGroup userGroup) {
                return new StringBuilder().append(userGroup.getName()).append(' ').append(userGroup.getBibWrites()).toString();
            }
        };
    }

    public Converter<Long, UserGroup> getIdToUserGroupConverter() {
        return new Converter<Long, UserGroup>() {
            @Override
            public UserGroup convert(Long id) {
                return UserGroup.findUserGroup(id);
            }
        };
    }

    public Converter<String, UserGroup> getStringToUserGroupConverter() {
        return new Converter<String, UserGroup>() {
            @Override
            public UserGroup convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), UserGroup.class);
            }
        };
    }

    @Autowired
    UserProfileService userProfileService;

    public Converter<Cart, String> getCartToStringConverter() {
        return new Converter<Cart, String>() {
            @Override
            public String convert(Cart cart) {
                return new StringBuilder().append(cart.getShipping()).append(' ').append(cart.getTotal()).append(' ').append(cart.getCreated()).append(' ')
                        .append(cart.getUpdated()).toString();
            }
        };
    }

    public Converter<Long, Cart> getIdToCartConverter() {
        return new Converter<Long, Cart>() {
            @Override
            public Cart convert(Long id) {
                return Cart.findCart(id);
            }
        };
    }

    public Converter<String, Cart> getStringToCartConverter() {
        return new Converter<String, Cart>() {
            @Override
            public Cart convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), Cart.class);
            }
        };
    }

    public Converter<CartItem, String> getCartItemToStringConverter() {
        return new Converter<CartItem, String>() {
            @Override
            public String convert(CartItem cartItem) {
                return new StringBuilder().append(cartItem.getQuantity()).append(' ').append(cartItem.getCreated()).append(' ').append(cartItem.getUpdated()).append(' ')
                        .append(cartItem.getComment()).toString();
            }
        };
    }

    public Converter<Long, CartItem> getIdToCartItemConverter() {
        return new Converter<Long, CartItem>() {
            @Override
            public CartItem convert(Long id) {
                return CartItem.findCartItem(id);
            }
        };
    }

    public Converter<String, CartItem> getStringToCartItemConverter() {
        return new Converter<String, CartItem>() {
            @Override
            public CartItem convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), CartItem.class);
            }
        };
    }

    public Converter<EventAlert, String> getEventAlertToStringConverter() {
        return new Converter<EventAlert, String>() {
            @Override
            public String convert(EventAlert eventAlert) {
                return new StringBuilder().append(eventAlert.getText()).toString();
            }
        };
    }

    public Converter<Long, EventAlert> getIdToEventAlertConverter() {
        return new Converter<Long, EventAlert>() {
            @Override
            public EventAlert convert(Long id) {
                return EventAlert.findEventAlert(id);
            }
        };
    }

    public Converter<String, EventAlert> getStringToEventAlertConverter() {
        return new Converter<String, EventAlert>() {
            @Override
            public EventAlert convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), EventAlert.class);
            }
        };
    }

    public Converter<EventCartItem, String> getEventCartItemToStringConverter() {
        return new Converter<EventCartItem, String>() {
            @Override
            public String convert(EventCartItem eventCartItem) {
                return new StringBuilder().append(eventCartItem.getName()).append(' ').append(eventCartItem.getDescription()).append(' ').append(eventCartItem.getPrice())
                        .append(' ').append(eventCartItem.getAvailable()).toString();
            }
        };
    }

    public Converter<Long, EventCartItem> getIdToEventCartItemConverter() {
        return new Converter<Long, EventCartItem>() {
            @Override
            public EventCartItem convert(Long id) {
                return EventCartItem.findEventCartItem(id);
            }
        };
    }

    public Converter<String, EventCartItem> getStringToEventCartItemConverter() {
        return new Converter<String, EventCartItem>() {
            @Override
            public EventCartItem convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), EventCartItem.class);
            }
        };
    }

    public Converter<EventCartItemPriceChange, String> getEventCartItemPriceChangeToStringConverter() {
        return new Converter<EventCartItemPriceChange, String>() {
            @Override
            public String convert(EventCartItemPriceChange eventCartItemPriceChange) {
                return new StringBuilder().append(eventCartItemPriceChange.getStartDate()).append(' ').append(eventCartItemPriceChange.getEndDate()).append(' ')
                        .append(eventCartItemPriceChange.getPrice()).toString();
            }
        };
    }

    public Converter<Long, EventCartItemPriceChange> getIdToEventCartItemPriceChangeConverter() {
        return new Converter<Long, EventCartItemPriceChange>() {
            @Override
            public EventCartItemPriceChange convert(Long id) {
                return EventCartItemPriceChange.findEventCartItemPriceChange(id);
            }
        };
    }

    public Converter<String, EventCartItemPriceChange> getStringToEventCartItemPriceChangeConverter() {
        return new Converter<String, EventCartItemPriceChange>() {
            @Override
            public EventCartItemPriceChange convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), EventCartItemPriceChange.class);
            }
        };
    }

    public Converter<EventMap, String> getEventMapToStringConverter() {
        return new Converter<EventMap, String>() {
            @Override
            public String convert(EventMap eventMap) {
                return new StringBuilder().append(eventMap.getUrl()).toString();
            }
        };
    }

    public Converter<Long, EventMap> getIdToEventMapConverter() {
        return new Converter<Long, EventMap>() {
            @Override
            public EventMap convert(Long id) {
                return EventMap.findEventMap(id);
            }
        };
    }

    public Converter<String, EventMap> getStringToEventMapConverter() {
        return new Converter<String, EventMap>() {
            @Override
            public EventMap convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), EventMap.class);
            }
        };
    }

    public Converter<EventPhoto, String> getEventPhotoToStringConverter() {
        return new Converter<EventPhoto, String>() {
            @Override
            public String convert(EventPhoto eventPhoto) {
                return new StringBuilder().append(eventPhoto.getUrl()).toString();
            }
        };
    }

    public Converter<Long, EventPhoto> getIdToEventPhotoConverter() {
        return new Converter<Long, EventPhoto>() {
            @Override
            public EventPhoto convert(Long id) {
                return EventPhoto.findEventPhoto(id);
            }
        };
    }

    public Converter<String, EventPhoto> getStringToEventPhotoConverter() {
        return new Converter<String, EventPhoto>() {
            @Override
            public EventPhoto convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), EventPhoto.class);
            }
        };
    }

    public Converter<EventResult, String> getEventResultToStringConverter() {
        return new Converter<EventResult, String>() {
            @Override
            public String convert(EventResult eventResult) {
                return new StringBuilder().append(eventResult.getText()).toString();
            }
        };
    }

    public Converter<Long, EventResult> getIdToEventResultConverter() {
        return new Converter<Long, EventResult>() {
            @Override
            public EventResult convert(Long id) {
                return EventResult.findEventResult(id);
            }
        };
    }

    public Converter<String, EventResult> getStringToEventResultConverter() {
        return new Converter<String, EventResult>() {
            @Override
            public EventResult convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), EventResult.class);
            }
        };
    }

    public Converter<EventType, String> getEventTypeToStringConverter() {
        return new Converter<EventType, String>() {
            @Override
            public String convert(EventType eventType) {
                return new StringBuilder().append(eventType.getTypeName()).append(' ').append(eventType.getStartTime()).toString();
            }
        };
    }

    public Converter<Long, EventType> getIdToEventTypeConverter() {
        return new Converter<Long, EventType>() {
            @Override
            public EventType convert(Long id) {
                return EventType.findEventType(id);
            }
        };
    }

    public Converter<String, EventType> getStringToEventTypeConverter() {
        return new Converter<String, EventType>() {
            @Override
            public EventType convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), EventType.class);
            }
        };
    }

    public Converter<EventUserGroup, String> getEventUserGroupToStringConverter() {
        return new Converter<EventUserGroup, String>() {
            @Override
            public String convert(EventUserGroup eventUserGroup) {
                return "(no displayable fields)";
            }
        };
    }

    public Converter<EventUserGroupId, EventUserGroup> getIdToEventUserGroupConverter() {
        return new Converter<EventUserGroupId, EventUserGroup>() {
            @Override
            public EventUserGroup convert(EventUserGroupId id) {
                return EventUserGroup.findEventUserGroup(id);
            }
        };
    }

    public Converter<String, EventUserGroup> getStringToEventUserGroupConverter() {
        return new Converter<String, EventUserGroup>() {
            @Override
            public EventUserGroup convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, EventUserGroupId.class), EventUserGroup.class);
            }
        };
    }

    public Converter<RaceImage, String> getRaceImageToStringConverter() {
        return new Converter<RaceImage, String>() {
            @Override
            public String convert(RaceImage raceImage) {
                return new StringBuilder().append(raceImage.getFilePath()).toString();
            }
        };
    }

    public Converter<Long, RaceImage> getIdToRaceImageConverter() {
        return new Converter<Long, RaceImage>() {
            @Override
            public RaceImage convert(Long id) {
                return RaceImage.findRaceImage(id);
            }
        };
    }

    public Converter<String, RaceImage> getStringToRaceImageConverter() {
        return new Converter<String, RaceImage>() {
            @Override
            public RaceImage convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), RaceImage.class);
            }
        };
    }

    public Converter<RaceResult, String> getRaceResultToStringConverter() {
        return new Converter<RaceResult, String>() {
            @Override
            public String convert(RaceResult raceResult) {
                return new StringBuilder().append(raceResult.getTimeofficialdisplay()).append(' ').append(raceResult.getBib()).append(' ').append(raceResult.getFirstname())
                        .append(' ').append(raceResult.getLastname()).toString();
            }
        };
    }

    public Converter<Long, RaceResult> getIdToRaceResultConverter() {
        return new Converter<Long, RaceResult>() {
            @Override
            public RaceResult convert(Long id) {
                return RaceResult.findRaceResult(id);
            }
        };
    }

    public Converter<String, RaceResult> getStringToRaceResultConverter() {
        return new Converter<String, RaceResult>() {
            @Override
            public RaceResult convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), RaceResult.class);
            }
        };
    }

    public Converter<ResultsFile, String> getResultsFileToStringConverter() {
        return new Converter<ResultsFile, String>() {
            @Override
            public String convert(ResultsFile resultsFile) {
                return new StringBuilder().append(resultsFile.getName()).append(' ').append(resultsFile.getContentType()).append(' ').append(resultsFile.getCreated()).append(' ')
                        .append(resultsFile.getFilesize()).toString();
            }
        };
    }

    public Converter<Long, ResultsFile> getIdToResultsFileConverter() {
        return new Converter<Long, ResultsFile>() {
            @Override
            public ResultsFile convert(Long id) {
                return ResultsFile.findResultsFile(id);
            }
        };
    }

    public Converter<String, ResultsFile> getStringToResultsFileConverter() {
        return new Converter<String, ResultsFile>() {
            @Override
            public ResultsFile convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), ResultsFile.class);
            }
        };
    }

    public Converter<ResultsFileMapping, String> getResultsFileMappingToStringConverter() {
        return new Converter<ResultsFileMapping, String>() {
            @Override
            public String convert(ResultsFileMapping resultsFileMapping) {
                return new StringBuilder().append(resultsFileMapping.getName()).append(' ').append(resultsFileMapping.getMap()).toString();
            }
        };
    }

    public Converter<Long, ResultsFileMapping> getIdToResultsFileMappingConverter() {
        return new Converter<Long, ResultsFileMapping>() {
            @Override
            public ResultsFileMapping convert(Long id) {
                return ResultsFileMapping.findResultsFileMapping(id);
            }
        };
    }

    public Converter<String, ResultsFileMapping> getStringToResultsFileMappingConverter() {
        return new Converter<String, ResultsFileMapping>() {
            @Override
            public ResultsFileMapping convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), ResultsFileMapping.class);
            }
        };
    }

    public Converter<ResultsImport, String> getResultsImportToStringConverter() {
        return new Converter<ResultsImport, String>() {
            @Override
            public String convert(ResultsImport resultsImport) {
                return new StringBuilder().append(resultsImport.getRunDate()).append(' ').append(resultsImport.getRowsProcessed()).append(' ').append(resultsImport.getErrors())
                        .append(' ').append(resultsImport.getErrorRows()).toString();
            }
        };
    }

    public Converter<Long, ResultsImport> getIdToResultsImportConverter() {
        return new Converter<Long, ResultsImport>() {
            @Override
            public ResultsImport convert(Long id) {
                return ResultsImport.findResultsImport(id);
            }
        };
    }

    public Converter<String, ResultsImport> getStringToResultsImportConverter() {
        return new Converter<String, ResultsImport>() {
            @Override
            public ResultsImport convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), ResultsImport.class);
            }
        };
    }

    public Converter<TimerConfig, String> getTimerConfigToStringConverter() {
        return new Converter<TimerConfig, String>() {
            @Override
            public String convert(TimerConfig timerConfig) {
                return new StringBuilder().append(timerConfig.getConnectionTimeout()).append(' ').append(timerConfig.getFilename()).append(' ').append(timerConfig.getPosition())
                        .append(' ').append(timerConfig.getUrl()).toString();
            }
        };
    }

    public Converter<Long, TimerConfig> getIdToTimerConfigConverter() {
        return new Converter<Long, TimerConfig>() {
            @Override
            public TimerConfig convert(Long id) {
                return TimerConfig.findTimerConfig(id);
            }
        };
    }

    public Converter<String, TimerConfig> getStringToTimerConfigConverter() {
        return new Converter<String, TimerConfig>() {
            @Override
            public TimerConfig convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), TimerConfig.class);
            }
        };
    }

    public Converter<Long, UserAuthority> getIdToUserAuthorityConverter() {
        return new Converter<Long, UserAuthority>() {
            @Override
            public UserAuthority convert(Long id) {
                return UserAuthority.findUserAuthority(id);
            }
        };
    }

    public Converter<String, UserAuthority> getStringToUserAuthorityConverter() {
        return new Converter<String, UserAuthority>() {
            @Override
            public UserAuthority convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), UserAuthority.class);
            }
        };
    }

    public Converter<UserGroupUserAuthority, String> getUserGroupUserAuthorityToStringConverter() {
        return new Converter<UserGroupUserAuthority, String>() {
            @Override
            public String convert(UserGroupUserAuthority userGroupUserAuthority) {
                return "(no displayable fields)";
            }
        };
    }

    public Converter<UserGroupUserAuthorityID, UserGroupUserAuthority> getIdToUserGroupUserAuthorityConverter() {
        return new Converter<UserGroupUserAuthorityID, UserGroupUserAuthority>() {
            @Override
            public UserGroupUserAuthority convert(UserGroupUserAuthorityID id) {
                return UserGroupUserAuthority.findUserGroupUserAuthority(id);
            }
        };
    }

    public Converter<String, UserGroupUserAuthority> getStringToUserGroupUserAuthorityConverter() {
        return new Converter<String, UserGroupUserAuthority>() {
            @Override
            public UserGroupUserAuthority convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, UserGroupUserAuthorityID.class), UserGroupUserAuthority.class);
            }
        };
    }

    public Converter<Long, UserProfile> getIdToUserProfileConverter() {
        return new Converter<Long, UserProfile>() {
            @Override
            public UserProfile convert(Long id) {
                return ApplicationConversionServiceFactoryBean.this.userProfileService.findUserProfile(id);
            }
        };
    }

    public Converter<String, UserProfile> getStringToUserProfileConverter() {
        return new Converter<String, UserProfile>() {
            @Override
            public UserProfile convert(String id) {
                return ApplicationConversionServiceFactoryBean.this.getObject().convert(ApplicationConversionServiceFactoryBean.this.getObject().convert(id, Long.class), UserProfile.class);
            }
        };
    }

    public Converter<String, UserGroupUserAuthorityID> getJsonToUserGroupUserAuthorityIDConverter() {
        return new Converter<String, UserGroupUserAuthorityID>() {
            @Override
            public UserGroupUserAuthorityID convert(String encodedJson) {
                return UserGroupUserAuthorityID.fromJsonToUserGroupUserAuthorityID(new String(Base64.decodeBase64(encodedJson)));
            }
        };
    }

    public Converter<UserGroupUserAuthorityID, String> getUserGroupUserAuthorityIDToJsonConverter() {
        return new Converter<UserGroupUserAuthorityID, String>() {
            @Override
            public String convert(UserGroupUserAuthorityID userGroupUserAuthorityID) {
                return Base64.encodeBase64URLSafeString(userGroupUserAuthorityID.toJson().getBytes());
            }
        };
    }

    public Converter<String, EventUserGroupId> getJsonToEventUserGroupIdConverter() {
        return new Converter<String, EventUserGroupId>() {
            @Override
            public EventUserGroupId convert(String encodedJson) {
                return EventUserGroupId.fromJsonToEventUserGroupId(new String(Base64.decodeBase64(encodedJson)));
            }
        };
    }

    public Converter<EventUserGroupId, String> getEventUserGroupIdToJsonConverter() {
        return new Converter<EventUserGroupId, String>() {
            @Override
            public String convert(EventUserGroupId eventUserGroupId) {
                return Base64.encodeBase64URLSafeString(eventUserGroupId.toJson().getBytes());
            }
        };
    }

    public void installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(this.getCartToStringConverter());
        registry.addConverter(this.getIdToCartConverter());
        registry.addConverter(this.getStringToCartConverter());
        registry.addConverter(this.getCartItemToStringConverter());
        registry.addConverter(this.getIdToCartItemConverter());
        registry.addConverter(this.getStringToCartItemConverter());
        registry.addConverter(this.getEventAlertToStringConverter());
        registry.addConverter(this.getIdToEventAlertConverter());
        registry.addConverter(this.getStringToEventAlertConverter());
        registry.addConverter(this.getEventCartItemToStringConverter());
        registry.addConverter(this.getIdToEventCartItemConverter());
        registry.addConverter(this.getStringToEventCartItemConverter());
        registry.addConverter(this.getEventCartItemPriceChangeToStringConverter());
        registry.addConverter(this.getIdToEventCartItemPriceChangeConverter());
        registry.addConverter(this.getStringToEventCartItemPriceChangeConverter());
        registry.addConverter(this.getEventMapToStringConverter());
        registry.addConverter(this.getIdToEventMapConverter());
        registry.addConverter(this.getStringToEventMapConverter());
        registry.addConverter(this.getEventPhotoToStringConverter());
        registry.addConverter(this.getIdToEventPhotoConverter());
        registry.addConverter(this.getStringToEventPhotoConverter());
        registry.addConverter(this.getEventResultToStringConverter());
        registry.addConverter(this.getIdToEventResultConverter());
        registry.addConverter(this.getStringToEventResultConverter());
        registry.addConverter(this.getEventTypeToStringConverter());
        registry.addConverter(this.getIdToEventTypeConverter());
        registry.addConverter(this.getStringToEventTypeConverter());
        registry.addConverter(this.getEventUserGroupToStringConverter());
        registry.addConverter(this.getIdToEventUserGroupConverter());
        registry.addConverter(this.getStringToEventUserGroupConverter());
        registry.addConverter(this.getRaceImageToStringConverter());
        registry.addConverter(this.getIdToRaceImageConverter());
        registry.addConverter(this.getStringToRaceImageConverter());
        registry.addConverter(this.getRaceResultToStringConverter());
        registry.addConverter(this.getIdToRaceResultConverter());
        registry.addConverter(this.getStringToRaceResultConverter());
        registry.addConverter(this.getResultsFileToStringConverter());
        registry.addConverter(this.getIdToResultsFileConverter());
        registry.addConverter(this.getStringToResultsFileConverter());
        registry.addConverter(this.getResultsFileMappingToStringConverter());
        registry.addConverter(this.getIdToResultsFileMappingConverter());
        registry.addConverter(this.getStringToResultsFileMappingConverter());
        registry.addConverter(this.getResultsImportToStringConverter());
        registry.addConverter(this.getIdToResultsImportConverter());
        registry.addConverter(this.getStringToResultsImportConverter());
        registry.addConverter(this.getTimerConfigToStringConverter());
        registry.addConverter(this.getIdToTimerConfigConverter());
        registry.addConverter(this.getStringToTimerConfigConverter());
        registry.addConverter(this.getUserAuthoritiesToStringConverter());
        registry.addConverter(this.getIdToUserAuthoritiesConverter());
        registry.addConverter(this.getStringToUserAuthoritiesConverter());
        registry.addConverter(this.getUserAuthorityToStringConverter());
        registry.addConverter(this.getIdToUserAuthorityConverter());
        registry.addConverter(this.getStringToUserAuthorityConverter());
        registry.addConverter(this.getUserGroupToStringConverter());
        registry.addConverter(this.getIdToUserGroupConverter());
        registry.addConverter(this.getStringToUserGroupConverter());
        registry.addConverter(this.getUserGroupUserAuthorityToStringConverter());
        registry.addConverter(this.getIdToUserGroupUserAuthorityConverter());
        registry.addConverter(this.getStringToUserGroupUserAuthorityConverter());
        registry.addConverter(this.getUserProfileToStringConverter());
        registry.addConverter(this.getIdToUserProfileConverter());
        registry.addConverter(this.getStringToUserProfileConverter());
        registry.addConverter(this.getJsonToUserGroupUserAuthorityIDConverter());
        registry.addConverter(this.getUserGroupUserAuthorityIDToJsonConverter());
        registry.addConverter(this.getJsonToEventUserGroupIdConverter());
        registry.addConverter(this.getEventUserGroupIdToJsonConverter());
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.installLabelConverters(this.getObject());
    }
}
