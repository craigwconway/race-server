package com.bibsmobile.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.PreUpdate;
import javax.persistence.TypedQuery;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Record of a transaction at an event. This contains a collection of Cart Items,
 * Responses to custom questions, a total (pre and post application of a bibs fee).
 * As carts are created, they get timed out if not completed after 10 minutes.
 * @author tobi
 *
 */
@Configurable
@Entity
public class Cart {

	/**
	 * New Cart. These can expire.
	 */
    public static final int NEW = 0;
    /**
     * Saved Cart. Unused.
     */
    public static final int SAVED = 1;
    /**
     * Processing cart. This cannot expire.
     */
    public static final int PROCESSING = 2;
    /**
     * Complete cart. This cannot expire.
     */
    public static final int COMPLETE = 3;
    /**
     * Cart refund requested. Unused.
     */
    public static final int REFUND_REQUEST = 4;
    /**
     * Refunded cart. This cannot expire.
     */
    public static final int REFUNDED = 5;

    /**
     * Expiration timeout in seconds.
     */
    public static final int DEFAULT_TIMEOUT = 600;

    /**
     * {@link UserProfile User} paying for cart. This can be an anonymous user.
     */
    @ManyToOne
    private UserProfile user;

    /**
     * Collection of {@link CartItem items} in cart.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "cart")
    private List<CartItem> cartItems;

    /**
     * Collection of {@link CustomRegFieldResponse responses} 
     * to organizer defined {@link CustomRegField questions}.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "cart")
    private List<CustomRegFieldResponse> customRegFieldResponses;
    
    /**
     * Payment processor for this cart.
     */
    @Enumerated
    private PaymentProviderEnum processor;
    /**
     * Cart total before processing fee is applied.
     */
    private long totalPreFee;
    /**
     * Total change in cart value due to custom questions.
     */
    private long questions;
    /**
     * Cart Total.
     */
    private long total;
    /**
     * Creation date of cart.
     */
    private Date created;
    /**
     * Last update date of cart.
     */
    private Date updated;
    /**
     * Status of cart. This is either {@link Cart#NEW NEW}, {@link Cart#PROCESSING PROCESSING},
     * {@link Cart#COMPLETE COMPLETE} or {@link Cart#REFUNDED REFUNDED}.
     */
    private int status;
    /**
     * Referral url generated on cart creation. This is used to credit users for
     * referring other users.
     */
    private String referralUrl;
    /**
     * Referring {@link Cart cart} object.
     */
    @OneToOne
    private Cart referral;
    /**
     * Switch denoting whether or not this cart has been shared.
     */
    private boolean shared;
    /**
     * Amount the total is modified by for social sharing discounts.
     */
    private long referralDiscount;
    /**
     * Switch to denote the cart paid out.
     */
    private boolean paidOut = false;
    /**
     * Amount paid out to the user.
     */
    private long payoutAmount;
    /**
     * Amount held by the processor in this cart.
     */
    private long processorFee;

    /**
     * Coupon applied this cart. This does not modify prices in
     * {@link EventCartItemTypeEnum#DONATION DONATION} type items.
     */
    @ManyToOne
    private EventCartItemCoupon coupon;

    /**
     * Timeout set on this cart.
     */
    private int timeout;

    /**
     * ID of stripe charge token associated with this cart.
     */
    private String stripeChargeId;
    /**
     * ID of stripe refund token associated with this cart.
     */
    private String stripeRefundId;

    
    @PreUpdate
    protected void onUpdate() {
        if (this.created == null)
            this.created = new Date();
        this.updated = new Date();
    }
    /**
     * JSON Deserializer function for carts. This includes the associated {@link UserProfile user}.
     * @param json JSON String to deserialize
     * @return Deserialized JSON object.
     */
    public static Cart fromJsonToCartWithUser(String json) {
        return new JSONDeserializer<Cart>().use(null, Cart.class).use("user", UserProfile.class).deserialize(json);
    }

    /**
     * This gets the {@link Event event} associated with the ticket in this cart.
     * @return Event object
     */
    public Event getEvent() {
        if (!this.getCartItems().isEmpty()) {
            return this.getCartItems().get(0).getEventCartItem().getEvent();
        }
        return null;
    }

    /**
     * Gets the {@link EventType event type} object associated with the ticket in this cart.
     * @return
     */
    public EventType getEventType() {
        if (!this.getCartItems().isEmpty()) {
            return this.getCartItems().get(0).getEventCartItem().getEventType();
        }
        return null;
    }

    /**
     * Gets the {@link #user} in this cart.
     * @return {@link UserProfile} object performing the transaction. 
     */
    public UserProfile getUser() {
        return this.user;
    }

    /**
     * Sets the {@link #user} in this cart.
     * @param user {@link UserProfile} object performing the transaction.
     */
    public void setUser(UserProfile user) {
        this.user = user;
    }

    public List<CartItem> getCartItems() {
        return this.cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public List<CustomRegFieldResponse> getCustomRegFieldResponses() {
        return this.customRegFieldResponses;
    }
    
    public void setCustomRegFieldResponses(List<CustomRegFieldResponse> customRegFieldResponses) {
        this.customRegFieldResponses = customRegFieldResponses;
    }
    
    public long getTotalPreFee() {
        return this.totalPreFee;
    }

    public void setTotalPreFee(long totalPreFee) {
        this.totalPreFee = totalPreFee;
    }

    /**
	 * @return the totalQuestions
	 */
	public long getQuestions() {
		return questions;
	}

	/**
	 * @param totalQuestions the totalQuestions to set
	 */
	public void setQuestions(long questions) {
		this.questions = questions;
	}

	public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return this.updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public EventCartItemCoupon getCoupon() {
        return this.coupon;
    }

    public void setCoupon(EventCartItemCoupon coupon) {
        this.coupon = coupon;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getStripeChargeId() {
        return this.stripeChargeId;
    }

    public void setStripeChargeId(String stripeChargeId) {
        this.stripeChargeId = stripeChargeId;
    }

    public String getStripeRefundId() {
        return this.stripeRefundId;
    }

    public void setStripeRefundId(String stripeRefundId) {
        this.stripeRefundId = stripeRefundId;
    }

    /**
	 * @return the referral
	 */
	public Cart getReferral() {
		return referral;
	}

	/**
	 * @param referral the referral to set
	 */
	public void setReferral(Cart referral) {
		this.referral = referral;
	}

	/**
	 * @return the referralUrl
	 */
	public String getReferralUrl() {
		return referralUrl;
	}

	/**
	 * @param referralUrl the referralUrl to set
	 */
	public void setReferralUrl(String referralUrl) {
		this.referralUrl = referralUrl;
	}

	/**
	 * @return the shared
	 */
	public boolean isShared() {
		return shared;
	}

	/**
	 * @param shared the shared to set
	 */
	public void setShared(boolean shared) {
		this.shared = shared;
	}

	/**
	 * @return the referralDiscount
	 */
	public long getReferralDiscount() {
		return referralDiscount;
	}

	/**
	 * @param referralDiscount the referralDiscount to set
	 */
	public void setReferralDiscount(long referralDiscount) {
		this.referralDiscount = referralDiscount;
	}

	/**
	 * @return the processor
	 */
	public PaymentProviderEnum getProcessor() {
		return processor;
	}
	/**
	 * @param processor the processor to set
	 */
	public void setProcessor(PaymentProviderEnum processor) {
		this.processor = processor;
	}
	/**
	 * @return the paidOut
	 */
	public boolean isPaidOut() {
		return paidOut;
	}
	/**
	 * @param paidOut the paidOut to set
	 */
	public void setPaidOut(boolean paidOut) {
		this.paidOut = paidOut;
	}
	/**
	 * @return the payoutAmount
	 */
	public long getPayoutAmount() {
		return payoutAmount;
	}
	/**
	 * @param payoutAmount the payoutAmount to set
	 */
	public void setPayoutAmount(long payoutAmount) {
		this.payoutAmount = payoutAmount;
	}
	/**
	 * @return the processorFee
	 */
	public long getProcessorFee() {
		return processorFee;
	}
	/**
	 * @param processorFee the processorFee to set
	 */
	public void setProcessorFee(long processorFee) {
		this.processorFee = processorFee;
	}
	@Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj.getClass() != this.getClass()) return false;
        Cart rhs = (Cart) obj;
        return new EqualsBuilder().append(this.coupon, rhs.coupon).append(this.created, rhs.created).append(this.id, rhs.id).append(this.totalPreFee, rhs.totalPreFee)
                .append(this.status, rhs.status).append(this.stripeChargeId, rhs.stripeChargeId).append(this.timeout, rhs.timeout).append(this.total, rhs.total)
                .append(this.updated, rhs.updated).append(this.user, rhs.user).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.coupon).append(this.created).append(this.id).append(this.totalPreFee).append(this.status).append(this.stripeChargeId)
                .append(this.timeout).append(this.total).append(this.updated).append(this.user).toHashCode();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("NEW", "SAVED", "PROCESSING", "COMPLETE", "REFUND_REQUEST", "REFUNDED",
            "DEFAULT_TIMEOUT", "user", "cartItems", "totalPreFee", "total", "created", "updated", "status", "coupons", "timeout", "stripeChargeId");

    public static EntityManager entityManager() {
        EntityManager em = new Cart().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countCarts() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Cart o", Long.class).getSingleResult();
    }
    
    
    public static List<Cart> findAllCarts() {
        return entityManager().createQuery("SELECT o FROM Cart o", Cart.class).getResultList();
    }

    public static List<Cart> findAllCarts(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Cart o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Cart.class).getResultList();
    }

    public static Cart findCart(Long id) {
        if (id == null)
            return null;
        return entityManager().find(Cart.class, id);
    }

    public static List<Cart> findCartEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Cart o", Cart.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<Cart> findCartEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Cart o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Cart.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    @Transactional
    public void persist() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Cart attached = Cart.findCart(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public void clear() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.clear();
    }

    @Transactional
    public Cart merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        Cart merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }
    
    public String toJsonForCartReturn() {
    	return new JSONSerializer()
    	.include("cartItems")
    	.include("cartItems.user")
    	.exclude("cartItems.eventType.event")
    	.exclude("cartItems.eventCartItem.event")
    	.exclude("cartItems.eventCartItem.eventType.event")
    	.exclude("event.raceResults")
    	.exclude("event.latestImportFile")
    	.exclude("event.awardCategorys")
    	.exclude("eventType")
    	.exclude("*.class").serialize(this);
    }

    public static Cart fromJsonToCart(String json) {
        return new JSONDeserializer<Cart>().use(null, Cart.class).deserialize(json);
    }

    public static String toJsonArray(Collection<Cart> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<Cart> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<Cart> fromJsonArrayToCarts(String json) {
        return new JSONDeserializer<List<Cart>>().use("values", Cart.class).deserialize(json);
    }

    public static TypedQuery<Cart> findCompletedCartsByEventCartItems(List<EventCartItem> eventCartItems, Date greaterThan, Date lessThan) {
        if (eventCartItems == null)
            throw new IllegalArgumentException("The eventCartItems argument is required");
        EntityManager em = CartItem.entityManager();
        //SELECT o FROM CartItem AS o join o.cart c WHERE o.bib is null
        String jpaQuery = "SELECT DISTINCT o FROM Cart AS o join o.cartItems c WHERE c.eventCartItem IN (:eventCartItems) and o.status = 3";
        if (greaterThan != null) {
            jpaQuery += " AND o.created > :fromDate";
        }
        if (lessThan != null) {
            jpaQuery += " AND o.created < :toDate";
        }
        TypedQuery<Cart> q = em.createQuery(jpaQuery, Cart.class);
        q.setParameter("eventCartItems", eventCartItems);
        if (greaterThan != null) {
            q.setParameter("fromDate", greaterThan);
        }
        if (lessThan != null) {
            q.setParameter("toDate", lessThan);
        }
        return q;
    }    

    public static TypedQuery<Cart> findCompletedCartsItemsByEventBeforeDate(Event event, Date timestamp) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = CartItem.entityManager();
        //SELECT o FROM CartItem AS o join o.cart c WHERE o.bib is null
        String jpaQuery = "SELECT DISTINCT o FROM Cart AS o join o.cartItems c join c.eventCartItem eci join eci.event e WHERE "
        		+ "e = :event and o.status = 3 and o.created < :timecheck";
        TypedQuery<Cart> q = em.createQuery(jpaQuery, Cart.class);
        q.setParameter("event", event);
        q.setParameter("timeCheck", timestamp);

        return q;
    }      

    public static TypedQuery<Cart> findRefundedCartsItemsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = CartItem.entityManager();
        //SELECT o FROM CartItem AS o join o.cart c WHERE o.bib is null
        String jpaQuery = "SELECT DISTINCT o FROM Cart AS o join o.cartItems c join c.eventCartItem eci join eci.event e WHERE "
        		+ "e = :event and o.status = 5";
        TypedQuery<Cart> q = em.createQuery(jpaQuery, Cart.class);
        q.setParameter("event", event);

        return q;
    }       
    
    public static TypedQuery<Cart> findRefundedCartsByEventCartItems(List<EventCartItem> eventCartItems, Date greaterThan, Date lessThan) {
        if (eventCartItems == null)
            throw new IllegalArgumentException("The eventCartItems argument is required");
        EntityManager em = CartItem.entityManager();
        //SELECT o FROM CartItem AS o join o.cart c WHERE o.bib is null
        String jpaQuery = "SELECT DISTINCT o FROM Cart AS o join o.cartItems c WHERE c.eventCartItem IN (:eventCartItems) and o.status = 5";
        if (greaterThan != null) {
            jpaQuery += " AND o.created > :fromDate";
        }
        if (lessThan != null) {
            jpaQuery += " AND o.created < :toDate";
        }
        TypedQuery<Cart> q = em.createQuery(jpaQuery, Cart.class);
        q.setParameter("eventCartItems", eventCartItems);
        if (greaterThan != null) {
            q.setParameter("fromDate", greaterThan);
        }
        if (lessThan != null) {
            q.setParameter("toDate", lessThan);
        }
        return q;
    }       
    
    public static Long countFindCartsByUser(UserProfile user) {
        if (user == null)
            throw new IllegalArgumentException("The user argument is required");
        EntityManager em = Cart.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM Cart AS o WHERE o.user = :user", Long.class);
        q.setParameter("user", user);
        return q.getSingleResult();
    }

    public static TypedQuery<Cart> findCartsByUser(UserProfile user) {
        if (user == null)
            throw new IllegalArgumentException("The user argument is required");
        EntityManager em = Cart.entityManager();
        TypedQuery<Cart> q = em.createQuery("SELECT o FROM Cart AS o WHERE o.user = :user", Cart.class);
        q.setParameter("user", user);
        return q;
    }

    public static TypedQuery<Cart> findCartsByUser(UserProfile user, String sortFieldName, String sortOrder) {
        if (user == null)
            throw new IllegalArgumentException("The user argument is required");
        EntityManager em = Cart.entityManager();
        String jpaQuery = "SELECT o FROM Cart AS o WHERE o.user = :user";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<Cart> q = em.createQuery(jpaQuery, Cart.class);
        q.setParameter("user", user);
        return q;
    }

    /**
     * Autogenerated ID in this cart. Serves as a primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    /**
     * Versioning number for this cart.
     */
    @Version
    @Column(name = "version")
    private Integer version;

    /**
     * Gets the {@link #id} of this cart.
     * @return ID of this cart.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the {@link #id} of the this cart.
     * @param id Id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the {@link #version} number of this cart to handle updates.
     * @return
     */
    public Integer getVersion() {
        return this.version;
    }

    /**
     * Sets the {@link #version} number of this cart to handle updates.
     * @param version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }
}
