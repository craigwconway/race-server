package com.bibsmobile.util;

public final class BuildType {
	private static BuildTypeName typeName = BuildTypeName.MASTER;
	private static boolean licensing = false;
	private static boolean registration = true;
	
	public BuildTypeName getTypeName() {
		return typeName;
	}
	
	public boolean isLicensing() {
		return licensing;
	}
	
	public boolean isRegistration() {
		return registration;
	}
}
