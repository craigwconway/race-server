package com.bibsmobile.model.wrapper;

import com.bibsmobile.model.UserProfile;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * Created by Jevgeni on 18.06.2014.
 */
@RooJavaBean
@RooToString
@RooEquals
@RooJson
public class UserProfileWrapper {
    private UserProfile userProfile;
    private String  userGroupName;

}
