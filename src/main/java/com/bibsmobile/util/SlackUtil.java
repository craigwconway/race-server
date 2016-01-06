package com.bibsmobile.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.Future;

import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemCoupon;
import com.bibsmobile.model.EventCartItemTypeEnum;
import com.google.gson.JsonObject;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class SlackUtil {
	private final static String reportsURL = "https://hooks.slack.com/services/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";
	
	private static String getIP() {
		String message = "";
		try {
			InetAddress hostIP = InetAddress.getLocalHost();
			String host = "[" + hostIP.getHostName() + " : " + hostIP.getHostAddress() + "] ";
			message += host;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
	
	public static void logReportAsync(String message) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		JsonObject json = new JsonObject();
		json.addProperty("text", message);
		Future<Response> f = asyncHttpClient.preparePost(reportsURL).setBody(json.toString()).execute();
	}

	public static void logEmailSend(String username, String eventname, String subject) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		JsonObject json = new JsonObject();
		String message = username + " has sent a message to registrants in event " + eventname + ": " + subject;
		json.addProperty("text", message);
		Future<Response> f = asyncHttpClient.preparePost(reportsURL).setBody(json.toString()).execute();
	}	
	
	public static void logRegAddECI(EventCartItem eci, String Eventname, String username) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		JsonObject json = new JsonObject();
		String message = getIP();
		message += username + " has created an ECI in event " + Eventname + ". Name: " + eci.getName() + " Type: " + eci.getType();
		if(eci.getType() != EventCartItemTypeEnum.DONATION) {
			message += " Quantity: " + eci.getAvailable();
		}
		json.addProperty("text", message);
		Future<Response> f = asyncHttpClient.preparePost(reportsURL).setBody(json.toString()).execute();
	}

	public static void logToggleRegistration(boolean mode, String Eventname, String username) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		JsonObject json = new JsonObject();
		String message = getIP();
		message += username + " has ";
		message += (mode == true) ? "enabled " : "disabled ";
		message += "registration in the event " + Eventname;
		json.addProperty("text", message);
		Future<Response> f = asyncHttpClient.preparePost(reportsURL).setBody(json.toString()).execute();
	}	
	
	public static void logRegAddCoupon(EventCartItemCoupon coupon, String Eventname, String username) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		JsonObject json = new JsonObject();
		String message = getIP();
		message += username + " has created " + coupon.getAvailable() + " coupons in event " + Eventname + ". Code: " + coupon.getCode();
		if(coupon.getDiscountAbsolute() != null) {
			message += ". " + coupon.getDiscountAbsolute() + " cents off.";
		} else {
			message += ". " + coupon.getDiscountRelative() + "% off.";
		}
		json.addProperty("text", message);
		Future<Response> f = asyncHttpClient.preparePost(reportsURL).setBody(json.toString()).execute();
	}
	
	public static void logRegExportAsync(String eventName, String eventType, int lowBib, int highBib, String username) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		JsonObject json = new JsonObject();
		String message = getIP();
		message += username + " has triggered an export of registration items in event: " + eventName + " to the type " + eventType;
		message += "ranging from bibs " + lowBib + " to " + highBib;
		json.addProperty("text", message);
		Future<Response> f = asyncHttpClient.preparePost(reportsURL).setBody(json.toString()).execute();
	}
	
}
