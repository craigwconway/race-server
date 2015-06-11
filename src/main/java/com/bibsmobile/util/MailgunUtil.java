package com.bibsmobile.util;

import com.bibsmobile.model.Event;
import com.bibsmobile.service.AbstractTimer;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.MediaType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public final class MailgunUtil {
    private static final int maxRecipients = 1000;
    public static final String CC_EMAIL = "hello@mybibs.co";

    private MailgunUtil() {
        super();
    }
    // BEGIN REG RECEIPT STRINGS
    public static final String REG_RECEIPT_ONE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n<head>\r\n<link href=\"https://fonts.googleapis.com/css?family=Open+Sans:400,300,700\" rel=\"stylesheet\" type=\"text/css\">\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<title>bibs register email</title>\r\n<style type=\"text/css\">body{margin:0;padding:0 0 20px 0;min-width:100%!important}.content{margin:30px auto;width:100%;max-width:600px;border:1px solid #e1e1e1;background:#fff;border-radius:20px}.header{padding:40px 20px 20px 20px;color:#423f3f;font-size:40px;font-family:'Open Sans',sans-serif;font-weight:300;border-bottom:1px solid #e1e1e1}.innerpadding{padding:30px 30px 30px 30px}.h1,.h2,.footer,.bodycopy,.info1,.info2{color:#423f3f;font-family:'Open Sans',sans-serif}.h4{padding:20px 0 0 0;font-size:18px;line-height:22px;font-weight:normal;}.h5{color:#423f3f;font-family:'Open Sans',sans-serif;font-weight:300}.h6{color:#b2b2b2;font-family:'Open Sans',sans-serif;font-weight:300}.h1{font-size:33px;line-height:38px;font-weight:300}.h2{padding:25px 0 0 0;font-size:26px;line-height:28px;font-weight:400}.h3{padding:0 0 15px 0;font-size:26px;line-height:28px;font-weight:normal}.h4{padding:25px 0 0 0;font-size:18px;line-height:22px;font-weight:normal}.h6{padding:52px 0 0 0;font-size:18px;line-height:22px;font-weight:normal}.h5{padding:25px 0 0 0;font-size:22px;line-height:24px;font-weight:normal}.bodycopy{padding:0 0 25px 0;font-size:16px;line-height:22px;border-bottom:1px solid #e1e1e1}}.info1{padding:0 0 20px 0;font-size:16px;line-height:22px;}.info2{padding:30px 0 0 0;font-size:40px;line-height:22px;font-weight:300}.footer{width:100%;clear:both}.aligncenter{text-align:center;padding:0 0 20px 0}</style>\r\n<style type=\"text/css\">@media only screen and (min-device-width:601px){.content{width:600px!important}}</style>\r\n</head>\r\n<body yahoo bgcolor=\"#f8f8f8\" style=\"margin:0;padding:0 0 20px 0;min-width:100%!important\">\r\n<table width=\"100%\" bgcolor=\"#f8f8f8\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\r\n<tr>\r\n<td>\r\n<!--[if (gte mso 9)|(IE)]>\r\n<table width=\"600\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\r\n<tr>\r\n<td>\r\n<![endif]-->\r\n<table class=\"content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"margin:30px auto;width:100%;max-width:600px;border:1px solid #e1e1e1;background:#fff;border-radius:20px\">\r\n<tr>\r\n<td></td>\r\n</tr>\r\n<tr>\r\n<td class=\"header\" style=\"padding:40px 20px 20px 20px;color:#423f3f;font-size:40px;font-family:'Open Sans',sans-serif;font-weight:300;border-bottom:1px solid #e1e1e1\">\r\n<table width=\"100%\" align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\r\n<tr>";
    // ONE FOLLOWED BY EVENT NAME
    public static final String REG_RECEIPT_TWO = "</tr>\r\n</table>\r\n</td>\r\n</tr>\r\n<tr>\r\n<td class=\"innerpadding borderbottom\" style=\"padding:30px 30px 30px 30px\">\r\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n<tr>\r\n<td class=\"bodycopy\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px;border-bottom:1px solid #e1e1e1\">\r\n<p>";
    // FOLLOWED BY ATHLETE FIRSTNAME
    public static final String REG_RECEIPT_THREE = ",</p>\r\n<p>Thanks for registering for the ";
    // FOLLOWED BY EVENT NAME
    public static final String REG_RECEIPT_FOUR = "! Below you will find your receipt for this event\r\nas well as the event details. Make sure all your information is correct and contact the\r\norganizer if you need additional assistance.</p>\r\n</td>\r\n</tr>\r\n</table>\r\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n<tr>\r\n<td class=\"h2\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:25px 0 0 0;font-size:26px;line-height:28px;font-weight:400\">\r\nReceipt\r\n</td>\r\n</tr>\r\n<tr>\r\n<td class=\"h4\" style=\"color:#b2b2b2;font-family:'Open Sans',sans-serif;font-weight:normal;padding:25px 0 0 0;font-size:18px;line-height:22px\">\r\nAthlete\r\n</td>\r\n</tr>\r\n<tr>\r\n<td class=\"info1\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px\">";
    // FOLLOWED BY ATHLETE FIRSTNAME + SPACE + LASTNAME
    public static final String REG_RECEIPT_FIVE = "</td>\r\n</tr>\r\n</table>\r\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n<tr>\r\n<td class=\"h4\" style=\"color:#b2b2b2;font-family:'Open Sans',sans-serif;font-weight:normal;padding:25px 0 0 0;font-size:18px;line-height:22px\">Item</td>\r\n<td class=\"h4\" style=\"color:#b2b2b2;font-family:'Open Sans',sans-serif;font-weight:normal;padding:25px 0 0 0;font-size:18px;line-height:22px\">Quantity</td>\r\n<td class=\"h4\" style=\"color:#b2b2b2;font-family:'Open Sans',sans-serif;font-weight:normal;padding:25px 0 0 0;font-size:18px;line-height:22px\">Price</td>\r\n</tr> <br>\r\n";
    // FOLLOWED BY A STRING + CARTITEMS
    public static final String REG_RECEIPT_SIX_A = "<tr>\r\n<td class=\"info1\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px\">";
    // FOLLOWED BY CARTITEM NAME
    public static final String REG_RECEIPT_SIX_B = "</td>\r\n<td class=\"info1\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px\">";
    // FOLLOWED BY CARTITEM QUANTITY
    public static final String REG_RECEIPT_SIX_C = "</td>\r\n<td class=\"info1\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px\">";
    // FOLLOWED BY CARTITEM PRICE
    public static final String REG_RECEIPT_SIX_D = "</td></tr>";
    // END LOOP, now is total string
    public static final String REG_RECEIPT_SEVEN_A = "<p>\r\n</p><tr>\r\n<td class=\"bodycopy\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px;border-bottom:1px solid #e1e1e1\"></td>\r\n</tr>\r\n<tr>\r\n<td class=\"h6\" style=\"color:#b2b2b2;font-family:'Open Sans',sans-serif;font-weight:normal;padding:52px 0 0 0;font-size:18px;line-height:22px\">Total: </td>\r\n<td class=\"info2\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:30px 0 0 0;font-size:40px;line-height:22px;font-weight:300\">";
    // FOLLOWED BY TOTAL
    public static final String REG_RECEIPT_SEVEN_B = "</td></tr></table>";
    // END OF REG RECEPT SECTION:
    public static final String REG_RECEIPT_EIGHT = "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n<tr>\r\n<td class=\"bodycopy\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px;border-bottom:1px solid #e1e1e1\"></td>\r\n<td class=\"bodycopy\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px;border-bottom:1px solid #e1e1e1\"></td>\r\n</tr>\r\n<tr>\r\n<td class=\"h2\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:25px 0 0 0;font-size:26px;line-height:28px;font-weight:400\">\r\nEvent Details\r\n</td>\r\n</tr>\r\n";
    // Event details header
    public static final String REG_RECEIPT_NINE_A = "<tr>\r\n<td class=\"h4\" style=\"color:#b2b2b2;font-family:'Open Sans',sans-serif;font-weight:normal;padding:25px 0 0 0;font-size:18px;line-height:22px\">\r\nStart time\r\n</td>\r\n</tr>\r\n<tr>\r\n<td class=\"info1\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px\">\r\nThis event starts at ";
    // followed by event starttime
    public static final String REG_RECEIPT_NINE_B = "</td></tr>";
    // Event Receipt section
    public static final String REG_RECEIPT_TEN_A = "<tr>\r\n<td class=\"h4\" style=\"color:#b2b2b2;font-family:'Open Sans',sans-serif;font-weight:normal;padding:25px 0 0 0;font-size:18px;line-height:22px\">\r\nReceipt:\r\n</td>\r\n</tr>\r\n<tr>\r\n<td class=\"info1\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px\"><a href=\"";
    // End of event receipt section
    public static final String REG_RECEIPT_TEN_B = "\">";
    public static final String REG_RECEIPT_TEN_C = "</a></td></tr>";
    // end event date section
    public static final String REG_RECEIPT_ELEVEN_A = "<tr>\r\n<td class=\"h4\" style=\"color:#b2b2b2;font-family:'Open Sans',sans-serif;font-weight:normal;padding:25px 0 0 0;font-size:18px;line-height:22px\">\r\nLocation of event\r\n</td>\r\n</tr>\r\n<tr>\r\n<td class=\"info1\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px\">";
    // followed by address, city, state with a <br> separating them
    public static final String REG_RECEIPT_ELEVEN_B = "</td></tr>";
    // end of location section
    public static final String REG_RECEIPT_TWELVE_A = "<tr>\r\n<td class=\"h4\" style=\"color:#b2b2b2;font-family:'Open Sans',sans-serif;font-weight:normal;padding:25px 0 0 0;font-size:18px;line-height:22px\">\r\nYour emergency contact\r\n</td>\r\n</tr>\r\n<tr>\r\n<td class=\"info1\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px\">";
    // emergency contact firstname, lastname, <br> phone here
    public static final String REG_RECEIPT_TWELVE_B = "</td></tr>";
    // end of emergency contact section
    public static final String REG_RECEIPT_THIRTEEN_A = "<tr>\r\n<td class=\"h5\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;font-weight:normal;padding:25px 0 0 0;font-size:22px;line-height:24px\">\r\nQuestions About The Event?\r\n</td>\r\n</tr>\r\n<tr>\r\n<td class=\"h4\" style=\"color:#b2b2b2;font-family:'Open Sans',sans-serif;font-weight:normal;padding:25px 0 0 0;font-size:18px;line-height:22px\">\r\nEvent organizer contact information\r\n</td>\r\n</tr>\r\n<tr>\r\n<td class=\"info1\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;padding:0 0 25px 0;font-size:16px;line-height:22px\">";
    // add in organizer email/phone after this
    public static final String REG_RECEIPT_THIRTEEN_B = "</td></tr>";
    // end of email
    public static final String REG_RECEIPT_FOURTEEN = "</table>\r\n</td>\r\n</tr>\r\n</table>\r\n<div class=\"footer\" style=\"color:#423f3f;font-family:'Open Sans',sans-serif;width:100%;clear:both\">\r\n<table width=\"100%\">\r\n<tr>\r\n<td class=\"aligncenter content-block\" style=\"text-align:center;padding:0 0 20px 0\">powered by <a href=\"http://www.mybibs.co\" target=\"_blank\" class=\"clink\"><img src=\"http://www.mybibs.co/wp-content/uploads/2014/10/bibsIcon11.png\" border=\"0\" id=\"sig-logo\" style=\"vertical-align:middle\"></a></td>\r\n</tr>\r\n</table>\r\n</div>\r\n<!--[if (gte mso 9)|(IE)]>\r\n</td>\r\n</tr>\r\n</table>\r\n<![endif]-->\r\n</td></tr></table>\r\n</body>\r\n</html>";
    
    public static boolean sendHTML(String to, String subject, String message) {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", "key-09244ca92420306315f8bf76f587f7fa"));
        WebResource webResource = client.resource("https://api.mailgun.net/v3/events.bibs.io/messages");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("from", "bibs <chef@events.bibs.io>");
        formData.add("to", to);
        formData.add("bcc", CC_EMAIL);
        formData.add("subject", subject);
        formData.add("html", message);
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        return (response.getStatus() == 200);
    }    
    
    public static boolean send(String to, String subject, String message) {
        return send(Collections.singletonList(to), subject, message);
    }

    public static boolean send(List<String> tos, String subject, String message) {
        return sendInternal(partitionRecipients(tos), subject, message);
    }

    private static boolean sendInternal(List<List<String>> toPartitions, String subject, String message) {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", "key-09244ca92420306315f8bf76f587f7fa"));
        WebResource webResource = client.resource("https://api.mailgun.net/v3/events.bibs.io/messages");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("from", "bibs <chef@events.bibs.io>");

        boolean success = true;
        for (List<String> tos : toPartitions) {
            String recipientVars = "{\"";
            for (String to : tos) {
                formData.add("to", to);
            }
            recipientVars += StringUtils.join(tos, "\": {}, \"");
            recipientVars += "\": {}}";
            formData.add("recipient-variables", recipientVars);
            formData.add("bcc", CC_EMAIL);
            formData.add("subject", subject);
            formData.add("text", message);
            ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);

            if (response.getStatus() != 200) {
                success = false;
                break;
            }
        }
        return success;
    }
    
    public static String googleEventReservationCard(Event event, Long cartId, String firstname, String lastname ) {
    	if(StringUtils.isEmpty(firstname) || StringUtils.isEmpty(lastname) || cartId == null || event == null) {
    		return "";
    	}
        Gson gson = new Gson();
        JsonObject responseObj = new JsonObject();
        responseObj.addProperty("@context", "http://schema.org");
        responseObj.addProperty("@type", "EventReservation");
        responseObj.addProperty("reservationStatus", "http://schema.org/Confirmed");
        JsonObject underName = new JsonObject();
        underName.addProperty("@type", "Person");
        underName.addProperty("name", firstname + " " + lastname);
        responseObj.add("underName", underName);
        JsonObject reservationFor = new JsonObject();
        reservationFor.addProperty("@type", "Event");
        reservationFor.addProperty("name", event.getName());
        // Get Local time:
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        if(event.getTimezone() != null) {
        	df.setTimeZone(event.getTimezone());
        } else {
        	df.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        reservationFor.addProperty("startDate", df.format(event.getTimeStart()));
        JsonObject location = new JsonObject();
        location.addProperty("@type", "Place");
        location.addProperty("name", event.getAddress());
        JsonObject postalAddress = new JsonObject();
        postalAddress.addProperty("@type", "PostalAddress");
        postalAddress.addProperty("streetAddress", event.getAddress());
        postalAddress.addProperty("addressLocality", event.getCity());
        postalAddress.addProperty("addressRegion", event.getState());
        postalAddress.addProperty("postalCode", event.getZip());
        postalAddress.addProperty("addressCountry", event.getCountry());
        location.add("address", postalAddress);
        reservationFor.add("location", location);
        return "<script type=\"application/ld+json\">" + gson.toJson(reservationFor) + "</script>";
    }

    public static List<List<String>> partitionRecipients(List<String> tos) {
        List<List<String>> partitions = new ArrayList<List<String>>(tos.size() / maxRecipients + 1);

        for (int i = 0; i < tos.size(); i += maxRecipients) {
            partitions.add(tos.subList(i, i + Math.min(maxRecipients, tos.size() - i)));
        }

        return partitions;
    }
}
