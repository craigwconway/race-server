package com.bibsmobile.util;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class MailgunUtil {
    public static final String CC_EMAIL = "hello@mybibs.co";

    public static boolean send(String to, String subject, String message) {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", "key-09244ca92420306315f8bf76f587f7fa"));
        WebResource webResource = client.resource("https://api.mailgun.net/v2/sandboxda951656aa49447398e9f8dd2cc9c8b3.mailgun.org/messages");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("from", "Mailgun Sandbox <postmaster@sandboxda951656aa49447398e9f8dd2cc9c8b3.mailgun.org>");
        formData.add("to", to);
        formData.add("bcc", CC_EMAIL);
        formData.add("subject", subject);
        formData.add("text", message);
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        return (response.getStatus() == 200);
    }
}