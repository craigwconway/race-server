package com.bibsmobile.util;

public final class BuildType {
	private static final BuildTypeName typeName = BuildTypeName.MASTER;
	private static final boolean licensing = true;
	private static final boolean registration = true;
	private static final boolean rfid = false;
	
	public BuildTypeName getTypeName() {
		return typeName;
	}
	
	public boolean isLicensing() {
		return licensing;
	}
	
	public boolean isRegistration() {
		return registration;
	}
	
	public boolean isRfid() {
		return rfid;
	}
}
