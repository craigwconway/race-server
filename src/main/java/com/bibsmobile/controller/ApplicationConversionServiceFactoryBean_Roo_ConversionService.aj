// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.controller;

import com.bibsmobile.controller.ApplicationConversionServiceFactoryBean;
import com.bibsmobile.model.Cart;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventAlert;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemPriceChange;
import com.bibsmobile.model.EventMap;
import com.bibsmobile.model.EventPhoto;
import com.bibsmobile.model.EventResult;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.ResultsFileMapping;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.model.TimerConfig;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserGroupUserAuthorityID;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.UserProfileService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

privileged aspect ApplicationConversionServiceFactoryBean_Roo_ConversionService {
    
    declare @type: ApplicationConversionServiceFactoryBean: @Configurable;
    
    @Autowired
    UserProfileService ApplicationConversionServiceFactoryBean.userProfileService;
    
    public Converter<Cart, String> ApplicationConversionServiceFactoryBean.getCartToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.Cart, java.lang.String>() {
            public String convert(Cart cart) {
                return new StringBuilder().append(cart.getShipping()).append(' ').append(cart.getTotal()).append(' ').append(cart.getCreated()).append(' ').append(cart.getUpdated()).toString();
            }
        };
    }
    
    public Converter<Long, Cart> ApplicationConversionServiceFactoryBean.getIdToCartConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.Cart>() {
            public com.bibsmobile.model.Cart convert(java.lang.Long id) {
                return Cart.findCart(id);
            }
        };
    }
    
    public Converter<String, Cart> ApplicationConversionServiceFactoryBean.getStringToCartConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.Cart>() {
            public com.bibsmobile.model.Cart convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Cart.class);
            }
        };
    }
    
    public Converter<Long, Event> ApplicationConversionServiceFactoryBean.getIdToEventConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.Event>() {
            public com.bibsmobile.model.Event convert(java.lang.Long id) {
                return Event.findEvent(id);
            }
        };
    }
    
    public Converter<String, Event> ApplicationConversionServiceFactoryBean.getStringToEventConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.Event>() {
            public com.bibsmobile.model.Event convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Event.class);
            }
        };
    }
    
    public Converter<EventAlert, String> ApplicationConversionServiceFactoryBean.getEventAlertToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventAlert, java.lang.String>() {
            public String convert(EventAlert eventAlert) {
                return new StringBuilder().append(eventAlert.getText()).toString();
            }
        };
    }
    
    public Converter<Long, EventAlert> ApplicationConversionServiceFactoryBean.getIdToEventAlertConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventAlert>() {
            public com.bibsmobile.model.EventAlert convert(java.lang.Long id) {
                return EventAlert.findEventAlert(id);
            }
        };
    }
    
    public Converter<String, EventAlert> ApplicationConversionServiceFactoryBean.getStringToEventAlertConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventAlert>() {
            public com.bibsmobile.model.EventAlert convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventAlert.class);
            }
        };
    }
    
    public Converter<EventCartItem, String> ApplicationConversionServiceFactoryBean.getEventCartItemToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventCartItem, java.lang.String>() {
            public String convert(EventCartItem eventCartItem) {
                return new StringBuilder().append(eventCartItem.getName()).append(' ').append(eventCartItem.getDescription()).append(' ').append(eventCartItem.getPrice()).append(' ').append(eventCartItem.getAvailable()).toString();
            }
        };
    }
    
    public Converter<Long, EventCartItem> ApplicationConversionServiceFactoryBean.getIdToEventCartItemConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventCartItem>() {
            public com.bibsmobile.model.EventCartItem convert(java.lang.Long id) {
                return EventCartItem.findEventCartItem(id);
            }
        };
    }
    
    public Converter<String, EventCartItem> ApplicationConversionServiceFactoryBean.getStringToEventCartItemConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventCartItem>() {
            public com.bibsmobile.model.EventCartItem convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventCartItem.class);
            }
        };
    }
    
    public Converter<EventCartItemPriceChange, String> ApplicationConversionServiceFactoryBean.getEventCartItemPriceChangeToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventCartItemPriceChange, java.lang.String>() {
            public String convert(EventCartItemPriceChange eventCartItemPriceChange) {
                return new StringBuilder().append(eventCartItemPriceChange.getStartDate()).append(' ').append(eventCartItemPriceChange.getEndDate()).append(' ').append(eventCartItemPriceChange.getPrice()).toString();
            }
        };
    }
    
    public Converter<Long, EventCartItemPriceChange> ApplicationConversionServiceFactoryBean.getIdToEventCartItemPriceChangeConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventCartItemPriceChange>() {
            public com.bibsmobile.model.EventCartItemPriceChange convert(java.lang.Long id) {
                return EventCartItemPriceChange.findEventCartItemPriceChange(id);
            }
        };
    }
    
    public Converter<String, EventCartItemPriceChange> ApplicationConversionServiceFactoryBean.getStringToEventCartItemPriceChangeConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventCartItemPriceChange>() {
            public com.bibsmobile.model.EventCartItemPriceChange convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventCartItemPriceChange.class);
            }
        };
    }
    
    public Converter<EventMap, String> ApplicationConversionServiceFactoryBean.getEventMapToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventMap, java.lang.String>() {
            public String convert(EventMap eventMap) {
                return new StringBuilder().append(eventMap.getUrl()).toString();
            }
        };
    }
    
    public Converter<Long, EventMap> ApplicationConversionServiceFactoryBean.getIdToEventMapConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventMap>() {
            public com.bibsmobile.model.EventMap convert(java.lang.Long id) {
                return EventMap.findEventMap(id);
            }
        };
    }
    
    public Converter<String, EventMap> ApplicationConversionServiceFactoryBean.getStringToEventMapConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventMap>() {
            public com.bibsmobile.model.EventMap convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventMap.class);
            }
        };
    }
    
    public Converter<EventPhoto, String> ApplicationConversionServiceFactoryBean.getEventPhotoToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventPhoto, java.lang.String>() {
            public String convert(EventPhoto eventPhoto) {
                return new StringBuilder().append(eventPhoto.getUrl()).toString();
            }
        };
    }
    
    public Converter<Long, EventPhoto> ApplicationConversionServiceFactoryBean.getIdToEventPhotoConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventPhoto>() {
            public com.bibsmobile.model.EventPhoto convert(java.lang.Long id) {
                return EventPhoto.findEventPhoto(id);
            }
        };
    }
    
    public Converter<String, EventPhoto> ApplicationConversionServiceFactoryBean.getStringToEventPhotoConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventPhoto>() {
            public com.bibsmobile.model.EventPhoto convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventPhoto.class);
            }
        };
    }
    
    public Converter<EventResult, String> ApplicationConversionServiceFactoryBean.getEventResultToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.EventResult, java.lang.String>() {
            public String convert(EventResult eventResult) {
                return new StringBuilder().append(eventResult.getText()).toString();
            }
        };
    }
    
    public Converter<Long, EventResult> ApplicationConversionServiceFactoryBean.getIdToEventResultConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.EventResult>() {
            public com.bibsmobile.model.EventResult convert(java.lang.Long id) {
                return EventResult.findEventResult(id);
            }
        };
    }
    
    public Converter<String, EventResult> ApplicationConversionServiceFactoryBean.getStringToEventResultConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.EventResult>() {
            public com.bibsmobile.model.EventResult convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), EventResult.class);
            }
        };
    }
    
    public Converter<RaceImage, String> ApplicationConversionServiceFactoryBean.getRaceImageToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.RaceImage, java.lang.String>() {
            public String convert(RaceImage raceImage) {
                return new StringBuilder().append(raceImage.getFilePath()).toString();
            }
        };
    }
    
    public Converter<Long, RaceImage> ApplicationConversionServiceFactoryBean.getIdToRaceImageConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.RaceImage>() {
            public com.bibsmobile.model.RaceImage convert(java.lang.Long id) {
                return RaceImage.findRaceImage(id);
            }
        };
    }
    
    public Converter<String, RaceImage> ApplicationConversionServiceFactoryBean.getStringToRaceImageConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.RaceImage>() {
            public com.bibsmobile.model.RaceImage convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), RaceImage.class);
            }
        };
    }
    
    public Converter<RaceResult, String> ApplicationConversionServiceFactoryBean.getRaceResultToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.RaceResult, java.lang.String>() {
            public String convert(RaceResult raceResult) {
                return new StringBuilder().append(raceResult.getTimeofficialdisplay()).append(' ').append(raceResult.getBib()).append(' ').append(raceResult.getFirstname()).append(' ').append(raceResult.getLastname()).toString();
            }
        };
    }
    
    public Converter<Long, RaceResult> ApplicationConversionServiceFactoryBean.getIdToRaceResultConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.RaceResult>() {
            public com.bibsmobile.model.RaceResult convert(java.lang.Long id) {
                return RaceResult.findRaceResult(id);
            }
        };
    }
    
    public Converter<String, RaceResult> ApplicationConversionServiceFactoryBean.getStringToRaceResultConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.RaceResult>() {
            public com.bibsmobile.model.RaceResult convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), RaceResult.class);
            }
        };
    }
    
    public Converter<ResultsFile, String> ApplicationConversionServiceFactoryBean.getResultsFileToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.ResultsFile, java.lang.String>() {
            public String convert(ResultsFile resultsFile) {
                return new StringBuilder().append(resultsFile.getName()).append(' ').append(resultsFile.getContentType()).append(' ').append(resultsFile.getCreated()).append(' ').append(resultsFile.getFilesize()).toString();
            }
        };
    }
    
    public Converter<Long, ResultsFile> ApplicationConversionServiceFactoryBean.getIdToResultsFileConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.ResultsFile>() {
            public com.bibsmobile.model.ResultsFile convert(java.lang.Long id) {
                return ResultsFile.findResultsFile(id);
            }
        };
    }
    
    public Converter<String, ResultsFile> ApplicationConversionServiceFactoryBean.getStringToResultsFileConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.ResultsFile>() {
            public com.bibsmobile.model.ResultsFile convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), ResultsFile.class);
            }
        };
    }
    
    public Converter<ResultsFileMapping, String> ApplicationConversionServiceFactoryBean.getResultsFileMappingToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.ResultsFileMapping, java.lang.String>() {
            public String convert(ResultsFileMapping resultsFileMapping) {
                return new StringBuilder().append(resultsFileMapping.getName()).append(' ').append(resultsFileMapping.getMap()).toString();
            }
        };
    }
    
    public Converter<Long, ResultsFileMapping> ApplicationConversionServiceFactoryBean.getIdToResultsFileMappingConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.ResultsFileMapping>() {
            public com.bibsmobile.model.ResultsFileMapping convert(java.lang.Long id) {
                return ResultsFileMapping.findResultsFileMapping(id);
            }
        };
    }
    
    public Converter<String, ResultsFileMapping> ApplicationConversionServiceFactoryBean.getStringToResultsFileMappingConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.ResultsFileMapping>() {
            public com.bibsmobile.model.ResultsFileMapping convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), ResultsFileMapping.class);
            }
        };
    }
    
    public Converter<ResultsImport, String> ApplicationConversionServiceFactoryBean.getResultsImportToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.ResultsImport, java.lang.String>() {
            public String convert(ResultsImport resultsImport) {
                return new StringBuilder().append(resultsImport.getRunDate()).append(' ').append(resultsImport.getRowsProcessed()).append(' ').append(resultsImport.getErrors()).append(' ').append(resultsImport.getErrorRows()).toString();
            }
        };
    }
    
    public Converter<Long, ResultsImport> ApplicationConversionServiceFactoryBean.getIdToResultsImportConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.ResultsImport>() {
            public com.bibsmobile.model.ResultsImport convert(java.lang.Long id) {
                return ResultsImport.findResultsImport(id);
            }
        };
    }
    
    public Converter<String, ResultsImport> ApplicationConversionServiceFactoryBean.getStringToResultsImportConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.ResultsImport>() {
            public com.bibsmobile.model.ResultsImport convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), ResultsImport.class);
            }
        };
    }
    
    public Converter<TimerConfig, String> ApplicationConversionServiceFactoryBean.getTimerConfigToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.TimerConfig, java.lang.String>() {
            public String convert(TimerConfig timerConfig) {
                return new StringBuilder().append(timerConfig.getPosition()).append(' ').append(timerConfig.getUrl()).append(' ').append(timerConfig.getReadTimeout()).append(' ').append(timerConfig.getReadPower()).toString();
            }
        };
    }
    
    public Converter<Long, TimerConfig> ApplicationConversionServiceFactoryBean.getIdToTimerConfigConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.TimerConfig>() {
            public com.bibsmobile.model.TimerConfig convert(java.lang.Long id) {
                return TimerConfig.findTimerConfig(id);
            }
        };
    }
    
    public Converter<String, TimerConfig> ApplicationConversionServiceFactoryBean.getStringToTimerConfigConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.TimerConfig>() {
            public com.bibsmobile.model.TimerConfig convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), TimerConfig.class);
            }
        };
    }
    
    public Converter<Long, UserAuthority> ApplicationConversionServiceFactoryBean.getIdToUserAuthorityConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.UserAuthority>() {
            public com.bibsmobile.model.UserAuthority convert(java.lang.Long id) {
                return UserAuthority.findUserAuthority(id);
            }
        };
    }
    
    public Converter<String, UserAuthority> ApplicationConversionServiceFactoryBean.getStringToUserAuthorityConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.UserAuthority>() {
            public com.bibsmobile.model.UserAuthority convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), UserAuthority.class);
            }
        };
    }
    
    public Converter<UserGroup, String> ApplicationConversionServiceFactoryBean.getUserGroupToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserGroup, java.lang.String>() {
            public String convert(UserGroup userGroup) {
                return new StringBuilder().append(userGroup.getName()).append(' ').append(userGroup.getBibWrites()).toString();
            }
        };
    }
    
    public Converter<Long, UserGroup> ApplicationConversionServiceFactoryBean.getIdToUserGroupConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.UserGroup>() {
            public com.bibsmobile.model.UserGroup convert(java.lang.Long id) {
                return UserGroup.findUserGroup(id);
            }
        };
    }
    
    public Converter<String, UserGroup> ApplicationConversionServiceFactoryBean.getStringToUserGroupConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.UserGroup>() {
            public com.bibsmobile.model.UserGroup convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), UserGroup.class);
            }
        };
    }
    
    public Converter<UserGroupUserAuthority, String> ApplicationConversionServiceFactoryBean.getUserGroupUserAuthorityToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserGroupUserAuthority, java.lang.String>() {
            public String convert(UserGroupUserAuthority userGroupUserAuthority) {
                return "(no displayable fields)";
            }
        };
    }
    
    public Converter<UserGroupUserAuthorityID, UserGroupUserAuthority> ApplicationConversionServiceFactoryBean.getIdToUserGroupUserAuthorityConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserGroupUserAuthorityID, com.bibsmobile.model.UserGroupUserAuthority>() {
            public com.bibsmobile.model.UserGroupUserAuthority convert(com.bibsmobile.model.UserGroupUserAuthorityID id) {
                return UserGroupUserAuthority.findUserGroupUserAuthority(id);
            }
        };
    }
    
    public Converter<String, UserGroupUserAuthority> ApplicationConversionServiceFactoryBean.getStringToUserGroupUserAuthorityConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.UserGroupUserAuthority>() {
            public com.bibsmobile.model.UserGroupUserAuthority convert(String id) {
                return getObject().convert(getObject().convert(id, UserGroupUserAuthorityID.class), UserGroupUserAuthority.class);
            }
        };
    }
    
    public Converter<Long, UserProfile> ApplicationConversionServiceFactoryBean.getIdToUserProfileConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.bibsmobile.model.UserProfile>() {
            public com.bibsmobile.model.UserProfile convert(java.lang.Long id) {
                return userProfileService.findUserProfile(id);
            }
        };
    }
    
    public Converter<String, UserProfile> ApplicationConversionServiceFactoryBean.getStringToUserProfileConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.UserProfile>() {
            public com.bibsmobile.model.UserProfile convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), UserProfile.class);
            }
        };
    }
    
    public Converter<String, UserGroupUserAuthorityID> ApplicationConversionServiceFactoryBean.getJsonToUserGroupUserAuthorityIDConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.bibsmobile.model.UserGroupUserAuthorityID>() {
            public UserGroupUserAuthorityID convert(String encodedJson) {
                return UserGroupUserAuthorityID.fromJsonToUserGroupUserAuthorityID(new String(Base64.decodeBase64(encodedJson)));
            }
        };
    }
    
    public Converter<UserGroupUserAuthorityID, String> ApplicationConversionServiceFactoryBean.getUserGroupUserAuthorityIDToJsonConverter() {
        return new org.springframework.core.convert.converter.Converter<com.bibsmobile.model.UserGroupUserAuthorityID, java.lang.String>() {
            public String convert(UserGroupUserAuthorityID userGroupUserAuthorityID) {
                return Base64.encodeBase64URLSafeString(userGroupUserAuthorityID.toJson().getBytes());
            }
        };
    }
    
    public void ApplicationConversionServiceFactoryBean.installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getCartToStringConverter());
        registry.addConverter(getIdToCartConverter());
        registry.addConverter(getStringToCartConverter());
        registry.addConverter(getEventToStringConverter());
        registry.addConverter(getIdToEventConverter());
        registry.addConverter(getStringToEventConverter());
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
    }
    
    public void ApplicationConversionServiceFactoryBean.afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
    
}
