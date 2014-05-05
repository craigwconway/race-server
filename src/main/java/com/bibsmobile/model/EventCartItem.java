package com.bibsmobile.model;
import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import javax.persistence.ManyToMany;

@RooJavaBean
@RooToString
@RooEquals
@RooJson
@RooJpaActiveRecord(finders = { "findEventCartItemsByEvent" })
public class EventCartItem {

    @ManyToOne
    private Event event;

    private String name;

    private String description;

    private double price;

    private int available;

    private int purchased;

    private String coupon;

    private double couponPrice;

    private int couponsAvailable;

    private int couponsUsed;

    private boolean timeLimit;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date timeStart;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date timeEnd;

    /**
     */
    @NotNull
    @Enumerated
    private EventCartItemTypeEnum type;

    /**
     */
    private double donationAmount;

    /**
     */
    private String charityName;

    /**
     */
    private String tshortSizes;

    /**
     */
    private String tshortColors;

    /**
     */
    private String tshortImageUrls;

    /**
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "eventCartItem")
    private Set<EventCartItemPriceChange> priceChanges;

    public double getActualPrice() {
        if (CollectionUtils.isEmpty(priceChanges)) {
            return price;
        }
        Date now = new Date();
        for (EventCartItemPriceChange priceChange : priceChanges) {
            if (now.after(priceChange.getStartDate()) && now.before(priceChange.getEndDate())) {
                return priceChange.getPrice();
            }
        }
        return price;
    }

}
