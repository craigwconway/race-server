package com.bibsmobile.model;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * Created by Jevgeni on 20.05.2014.
 */
@RooJavaBean
@RooToString
@RooJson
public class AuthorizeData {

    private String amount;
    private String xLogin;
    private String xFpSequence;
    private String xFpTimestamp;
    private String xFpHash;
    private String xRelayUrl;
    private String xInvoiceNum;
}
