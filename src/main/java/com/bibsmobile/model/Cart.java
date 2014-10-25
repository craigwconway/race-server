package com.bibsmobile.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
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

@Configurable
@Entity
public class Cart {

    public static final int NEW = 0;
    public static final int SAVED = 1;
    public static final int PROCESSING = 2;
    public static final int COMPLETE = 3;
    public static final int REFUND_REQUEST = 4;
    public static final int REFUNDED = 5;

    public static final int DEFAULT_TIMEOUT = 600;

    @ManyToOne
    private UserProfile user;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "cart")
    private List<CartItem> cartItems;

    private double shipping;
    private double total;
    private Date created;
    private Date updated;
    private int status;
    private String coupons;

    private int timeout;

    private String stripeChargeId;

    public static Cart fromJsonToCartWithUser(String json) {
        return new JSONDeserializer<Cart>().use(null, Cart.class).use("user", UserProfile.class).deserialize(json);
    }

    public UserProfile getUser() {
        return this.user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public List<CartItem> getCartItems() {
        return this.cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public double getShipping() {
        return this.shipping;
    }

    public void setShipping(double shipping) {
        this.shipping = shipping;
    }

    public double getTotal() {
        return this.total;
    }

    public void setTotal(double total) {
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

    public String getCoupons() {
        return this.coupons;
    }

    public void setCoupons(String coupons) {
        this.coupons = coupons;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cart)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Cart rhs = (Cart) obj;
        return new EqualsBuilder().append(this.coupons, rhs.coupons).append(this.created, rhs.created).append(this.id, rhs.id).append(this.shipping, rhs.shipping)
                .append(this.status, rhs.status).append(this.stripeChargeId, rhs.stripeChargeId).append(this.timeout, rhs.timeout).append(this.total, rhs.total)
                .append(this.updated, rhs.updated).append(this.user, rhs.user).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.coupons).append(this.created).append(this.id).append(this.shipping).append(this.status).append(this.stripeChargeId)
                .append(this.timeout).append(this.total).append(this.updated).append(this.user).toHashCode();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("NEW", "SAVED", "PROCESSING", "COMPLETE", "REFUND_REQUEST", "REFUNDED",
            "DEFAULT_TIMEOUT", "user", "cartItems", "shipping", "total", "created", "updated", "status", "coupons", "timeout", "stripeChargeId");

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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
