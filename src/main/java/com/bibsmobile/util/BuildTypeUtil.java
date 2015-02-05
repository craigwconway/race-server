/**
 * BUILD 
 */
package com.bibsmobile.util;

/**
 * @author galen
 *
 */
public class BuildTypeUtil {
	public enum BuildType {
		MASTER, DEVELOP, RFID, RFID_LICENSED
	}
	
	public static BuildType getBuildType() {
		return BuildType.MASTER;
	}
	
	public static boolean needsLicense() {
		BuildType type = getBuildType();
		if(BuildType.RFID_LICENSED == type) {
			return true;
		} else {
			return false;
		}
	}
}
