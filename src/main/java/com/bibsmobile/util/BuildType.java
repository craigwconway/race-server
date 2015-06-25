package com.bibsmobile.util;

public final class BuildType {
	private static final BuildTypeName typeName = BuildTypeName.MASTER;
	private static final String frontend = "https://bibs-frontend.herokuapp.com";
	private static final boolean licensing = false;
	private static final boolean registration = true;
	private static final boolean rfid = false;
	
	public BuildTypeName getTypeName() {
		return typeName;
	}
	
	public String getFrontend() {
		return frontend;
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
