/**
 * BUILD 
 */
package com.bibsmobile.util;

/**
 * @author galen
 *
 */
public class BuildTypeUtil {
	private static BuildType build;
	
	public static boolean usesLicensing() {
		return build.isLicensing();
	}
	
	public static boolean usesRegistration() {
		return build.isRegistration();
	}
	
	public static BuildType getBuild() {
		return build;
	}
	
}
