package com.bibsmobile.util;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserProfile;

public final class PermissionsUtil {
    private PermissionsUtil() {
        super();
    }

    /**
     * checks whether the given user has access/rights to the given event
     * i.e.:
     * - is a sys admin
     * - is a assigned event admin
     * @param user
     * @param event
     * @return
     */
    public static boolean isEventAdmin(UserProfile user, Event event) {
        // go through all assigned user authorities
        for (UserAuthorities uas : user.getUserAuthorities()) {
            UserAuthority ua = uas.getUserAuthority();
            // sys admins have all permissions anyways
            if (ua.isAuthority(UserAuthority.SYS_ADMIN)) {
                return true;
            }
            // event admins need more thorough checks
            if (ua.isAuthority(UserAuthority.EVENT_ADMIN)) {
                // check if assigned to this event
                for (UserGroupUserAuthority ugua : uas.getUserGroupUserAuthorities()) {
                    for (EventUserGroup eug : ugua.getUserGroup().getEventUserGroups()) {
                        if (eug.getEvent().equals(event)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
