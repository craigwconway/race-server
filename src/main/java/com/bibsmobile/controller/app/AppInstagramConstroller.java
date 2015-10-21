/**
 * 
 */
package com.bibsmobile.controller.app;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.StringBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jinstagram.*;
import org.jinstagram.realtime.SubscriptionResponseObject;
import org.jinstagram.realtime.SubscriptionUtil;
import org.jinstagram.realtime.InstagramSubscription;
import org.jinstagram.realtime.SubscriptionType;
import org.jinstagram.realtime.SubscriptionResponse;
import org.jinstagram.exceptions.InstagramException;
/**
 * Used to manage instagram integrations with the bibs service. A user can authorize
 * their instagram account through this or share in one of their photos.
 * @author galen
 *
 */
@Controller
@RequestMapping("/app/instagram")
public class AppInstagramConstroller {
	private static final String HUB_MODE = "hub.mode";
    private static final String HUB_CHALLENGE = "hub.challenge";
    private static final String HUB_VERIFY_TOKEN = "hub.verify_token";
    
    
    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handlePostRequest(req, resp);
    }

    @RequestMapping(value = "/webhook", produces = "text/plain", method = RequestMethod.GET)
    @ResponseBody
    protected String doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	System.out.println("[Instagram] Processing webhook");
    	
    	String hubChallenge = req.getParameter(HUB_CHALLENGE);
        String hubVerifyToken = req.getParameter(HUB_VERIFY_TOKEN);
        String hubMode = req.getParameter(HUB_MODE);
        return hubChallenge;
        //resp.getWriter().print(hubChallenge);
        
        /*
        handleCallbackURLVerification(req, resp);
        System.out.println("Response: " + resp);
        System.out.println("Response headers:" + resp.getHeaderNames());
        System.out.println("Response  status: " + resp.getStatus());
        */
    }   
    
    /**
     * Internal Function to handle getting callback URL from instagram
     */
    private void handleCallbackURLVerification(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Instagram will send the following parameter in your callback
        // http://your-callback.com/url/?hub.mode=subscribe&hub.challenge=15f7d1a91c1f40f8a748fd134752feb3&hub.verify_token=myVerifyToken
        String hubChallenge = req.getParameter(HUB_CHALLENGE);
        String hubVerifyToken = req.getParameter(HUB_VERIFY_TOKEN);
        String hubMode = req.getParameter(HUB_MODE);
        resp.getWriter().print(hubChallenge);
    }
    
    /**
     * Internal Function to handle getting callback POST from instagram
     */
    private void handlePostRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get the json data from the HttpServletRequest
        String jsonData = getBody(req);
        SubscriptionResponseObject[] subscriptionResponseDataList = SubscriptionUtil.getSubscriptionResponseData(jsonData);
        for (SubscriptionResponseObject subscriptionResponseObject : subscriptionResponseDataList) {
            // ObjectId is the name of the tag, i.e. #jinstagram
            //SessionHandler.sendMessage(subscriptionResponseObject.getObjectId());
        }
    }
    
    /**
     * Internal Function to get body from Instagram request
     */
    private static String getBody(HttpServletRequest request) throws IOException {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        body = stringBuilder.toString();
        return body;
    }

    @RequestMapping("/test")
    @ResponseBody
    public String subscribe() {
    	InstagramSubscription igSub = new InstagramSubscription()
        .clientId("e187dc60c88c4673a187200ad76b469b")
        .clientSecret("3f8a4c9912f54129be5aef1140b131c6")
        .object(SubscriptionType.TAGS)
        .objectId("london")
        .aspect("media")
        .callback("https://overmind.bibs.io/bibs-server/app/instagram/webhook")
        .verifyToken("londonTagSubscription");
    	System.out.println(igSub);
    	try {
        	SubscriptionResponse subscriptionResponse = igSub.createSubscription();
        	System.out.println("Subscription Response: " + subscriptionResponse + " Data: " + subscriptionResponse.getData());
        	String subscriptionId = subscriptionResponse.getData().getId();
        	System.out.println("Created subscription:  " + subscriptionId);
        	return "Now tracking type"  + subscriptionResponse.getData().getType() + " ID: " + subscriptionResponse.getData().getObject();
    	} catch (InstagramException e) {
    		System.out.println(e);
    	}
    	return "fuck you";

    }
}
