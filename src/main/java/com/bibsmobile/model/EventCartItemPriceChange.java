package com.bibsmobile.model;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = { "findEventCartItemPriceChangesByEventCartItem" })
public class EventCartItemPriceChange {

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date startDate;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date endDate;

    /**
     */
    @ManyToOne
    private EventCartItem eventCartItem;

    /**
     */
    private double price;
}
