package com.bibsmobile.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
public class CartItem {

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private EventCartItem eventCartItem;

    @OneToOne
    private EventType eventType;
    
    @ManyToOne
    private EventCartItemPriceChange priceChange;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

    @OneToOne(fetch = FetchType.LAZY)
    private UserGroup team;

    private int quantity;

    private Date created;

    private Date updated;

    private String comment;

    private Boolean exported;

    private String color;

    private String size;

    private long price;

    private Long bib;
    
    public static TypedQuery<CartItem> findCompletedCartItemsByEventCartItems(List<EventCartItem> eventCartItems, Date greaterThan, Date lessThan) {
        if (eventCartItems == null)
            throw new IllegalArgumentException("The eventCartItems argument is required");
        EntityManager em = CartItem.entityManager();
        //SELECT o FROM CartItem AS o join o.cart c WHERE o.bib is null
        String jpaQuery = "SELECT o FROM CartItem AS o join o.cart c WHERE o.eventCartItem IN (:eventCartItems) and c.status = 3";
        if (greaterThan != null) {
            jpaQuery += " AND o.created > :fromDate";
        }
        if (lessThan != null) {
            jpaQuery += " AND o.created < :toDate";
        }
        TypedQuery<CartItem> q = em.createQuery(jpaQuery, CartItem.class);
        q.setParameter("eventCartItems", eventCartItems);
        if (greaterThan != null) {
            q.setParameter("fromDate", greaterThan);
        }
        if (lessThan != null) {
            q.setParameter("toDate", lessThan);
        }
        return q;
    }

    public static TypedQuery<CartItem> findCartItemsByEventCartItems(List<EventCartItem> eventCartItems, Date greaterThan, Date lessThan) {
        if (eventCartItems == null)
            throw new IllegalArgumentException("The eventCartItems argument is required");
        EntityManager em = CartItem.entityManager();
        String jpaQuery = "SELECT o FROM CartItem AS o WHERE o.eventCartItem IN (:eventCartItems)";
        if (greaterThan != null) {
            jpaQuery += " AND o.created > :fromDate";
        }
        if (lessThan != null) {
            jpaQuery += " AND o.created < :toDate";
        }
        TypedQuery<CartItem> q = em.createQuery(jpaQuery, CartItem.class);
        q.setParameter("eventCartItems", eventCartItems);
        if (greaterThan != null) {
            q.setParameter("fromDate", greaterThan);
        }
        if (lessThan != null) {
            q.setParameter("toDate", lessThan);
        }
        return q;
    }    
    public static long countFindCompletedCartItemsByEventCartItems(List<EventCartItem> eventCartItems, boolean all) {
        if (eventCartItems == null)
            return 0;
        EntityManager em = CartItem.entityManager();
        String jpaQuery = "SELECT COUNT(o) FROM CartItem o AS o join o.cart c WHERE o.eventCartItem IN (:eventCartItems) and c.status = 3";
        if (!all) {
            jpaQuery += " AND o.exported != true";
        }
        TypedQuery<Long> q = em.createQuery(jpaQuery, Long.class);
        q.setParameter("eventCartItems", eventCartItems);
        return q.getSingleResult();
    }
    
    public static TypedQuery<CartItem> findCompletedCartItemsByEventCartItems(List<EventCartItem> eventCartItems, boolean all) {
        if (eventCartItems == null)
            throw new IllegalArgumentException("The eventCartItems argument is required");
        EntityManager em = CartItem.entityManager();
        String jpaQuery = "SELECT o FROM CartItem AS o WHERE o.eventCartItem IN (:eventCartItems) and o.cart.status = 3";
        if (!all) {
            jpaQuery += " AND o.exported != true";
        }
        TypedQuery<CartItem> q = em.createQuery(jpaQuery, CartItem.class);
        q.setParameter("eventCartItems", eventCartItems);
        return q;
    }

    public static List<CartItem> findAllCartItems(Date from, Date to) {
        EntityManager em = CartItem.entityManager();
        String jpaQuery = "SELECT o FROM CartItem AS o";
        if (from != null && to != null) {
            jpaQuery = "SELECT o FROM CartItem AS o WHERE o.created > :fromDate AND o.created < :toDate";
        } else if (from != null) {
            jpaQuery = "SELECT o FROM CartItem AS o WHERE o.created > :fromDate";
        } else if (to != null) {
            jpaQuery = "SELECT o FROM CartItem AS o WHERE o.created < :toDate";
        }
        TypedQuery<CartItem> q = em.createQuery(jpaQuery, CartItem.class);
        if (from != null) {
            q.setParameter("fromDate", from);
        }
        if (to != null) {
            q.setParameter("toDate", to);
        }
        return q.getResultList();
    }

    public Cart getCart() {
        return this.cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public EventCartItem getEventCartItem() {
        return this.eventCartItem;
    }

    public void setEventCartItem(EventCartItem eventCartItem) {
        this.eventCartItem = eventCartItem;
    }

    public EventCartItemPriceChange getEventCartItemPriceChange() {
    	return this.priceChange;
    }
    
    public void setEventCartItemPriceChange(EventCartItemPriceChange priceChange) {
    	this.priceChange = priceChange;
    }
    
    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserGroup getTeam() {
        return team;
    }

    public void setTeam(UserGroup team) {
        this.team = team;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getExported() {
        return this.exported;
    }

    public void setExported(Boolean exported) {
        this.exported = exported;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    // HACK
    // TODO: fix quickfix on price to something cooler
    public long getPrice() {
        return this.price / 100;
    }

    public void setPrice(long price) {
        this.price = price * 100;
    }

    /**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the bib
	 */
	public Long getBib() {
		return bib;
	}

	/**
	 * @param bib the bib to set
	 */
	public void setBib(Long bib) {
		this.bib = bib;
	}

	@PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("cart", "eventCartItem", "userProfile", "quantity", "created", "updated", "comment",
            "coupon", "exported", "color", "size", "price");

    public static EntityManager entityManager() {
        EntityManager em = new CartItem().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countCartItems() {
        return entityManager().createQuery("SELECT COUNT(o) FROM CartItem o", Long.class).getSingleResult();
    }

    public static List<CartItem> findAllCartItems() {
        return entityManager().createQuery("SELECT o FROM CartItem o", CartItem.class).getResultList();
    }

    public static List<CartItem> findAllCartItems(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM CartItem o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, CartItem.class).getResultList();
    }

    public static CartItem findCartItem(Long id) {
        if (id == null)
            return null;
        return entityManager().find(CartItem.class, id);
    }

    public static List<CartItem> findCartItemEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM CartItem o", CartItem.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<CartItem> findCartItemEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM CartItem o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, CartItem.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            CartItem attached = CartItem.findCartItem(this.id);
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
    public CartItem merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        CartItem merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj.getClass() != this.getClass()) return false;
        CartItem rhs = (CartItem) obj;
        return new EqualsBuilder().append(this.cart, rhs.cart).append(this.color, rhs.color).append(this.comment, rhs.comment)
                .append(this.created, rhs.created).append(this.eventCartItem, rhs.eventCartItem).append(this.exported, rhs.exported).append(this.id, rhs.id)
                .append(this.price, rhs.price).append(this.quantity, rhs.quantity).append(this.size, rhs.size).append(this.updated, rhs.updated)
                .append(this.userProfile, rhs.userProfile).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.cart).append(this.color).append(this.comment).append(this.created).append(this.eventCartItem)
                .append(this.exported).append(this.id).append(this.price).append(this.quantity).append(this.size).append(this.updated).append(this.userProfile).toHashCode();
    }

    public static Long countFindCartItemsByCreatedGreaterThan(Date created) {
        if (created == null)
            throw new IllegalArgumentException("The created argument is required");
        EntityManager em = CartItem.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM CartItem AS o WHERE o.created > :created", Long.class);
        q.setParameter("created", created);
        return q.getSingleResult();
    }

    public static Long countFindCartItemsByEventCartItem(EventCartItem eventCartItem) {
        if (eventCartItem == null)
            throw new IllegalArgumentException("The eventCartItem argument is required");
        EntityManager em = CartItem.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM CartItem AS o WHERE o.eventCartItem = :eventCartItem", Long.class);
        q.setParameter("eventCartItem", eventCartItem);
        return q.getSingleResult();
    }

    public static TypedQuery<CartItem> findCartItemsByCreatedGreaterThan(Date created) {
        if (created == null)
            throw new IllegalArgumentException("The created argument is required");
        EntityManager em = CartItem.entityManager();
        TypedQuery<CartItem> q = em.createQuery("SELECT o FROM CartItem AS o WHERE o.created > :created", CartItem.class);
        q.setParameter("created", created);
        return q;
    }
 
    public static TypedQuery<CartItem> findUnmappedCompletedCartItemsByEventType(EventType eventType) {
        if (eventType == null)
            throw new IllegalArgumentException("The created argument is required");
        
        EntityManager em = CartItem.entityManager();
        TypedQuery<CartItem> q = em.createQuery("SELECT o FROM CartItem AS o join o.cart c WHERE o.bib is null and o.eventType = :eventType and c.status = 3", CartItem.class);
        q.setParameter("eventType", eventType);
        return q;
    }       

    public static TypedQuery<CartItem> findCompletedCartItemsByEventType(EventType eventType) {
        if (eventType == null)
            throw new IllegalArgumentException("The created argument is required");
        
        EntityManager em = CartItem.entityManager();
        TypedQuery<CartItem> q = em.createQuery("SELECT o FROM CartItem AS o join o.cart c WHERE o.eventType = :eventType and c.status = 3", CartItem.class);
        q.setParameter("eventType", eventType);
        return q;
    }    
    
    public static TypedQuery<CartItem> findUnmappedCartItemsByEventType(EventType eventType) {
        if (eventType == null)
            throw new IllegalArgumentException("The created argument is required");
        
        EntityManager em = CartItem.entityManager();
        TypedQuery<CartItem> q = em.createQuery("SELECT o FROM CartItem AS o WHERE o.bib is null and o.eventType = :eventType", CartItem.class);
        q.setParameter("eventType", eventType);
        return q;
    }    
    
    public static TypedQuery<CartItem> findCartItemsByEventType(EventType eventType) {
        if (eventType == null)
            throw new IllegalArgumentException("The created argument is required");
        
        EntityManager em = CartItem.entityManager();
        TypedQuery<CartItem> q = em.createQuery("SELECT o FROM CartItem AS o WHERE o.eventType = :created", CartItem.class);
        q.setParameter("eventType", eventType);
        return q;
    }
    
    public static TypedQuery<CartItem> findCartItemsByCreatedGreaterThan(Date created, String sortFieldName, String sortOrder) {
        if (created == null)
            throw new IllegalArgumentException("The created argument is required");
        EntityManager em = CartItem.entityManager();
        String jpaQuery = "SELECT o FROM CartItem AS o WHERE o.created > :created";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<CartItem> q = em.createQuery(jpaQuery, CartItem.class);
        q.setParameter("created", created);
        return q;
    }

    public static TypedQuery<CartItem> findCartItemsByEventCartItem(EventCartItem eventCartItem) {
        if (eventCartItem == null)
            throw new IllegalArgumentException("The eventCartItem argument is required");
        EntityManager em = CartItem.entityManager();
        TypedQuery<CartItem> q = em.createQuery("SELECT o FROM CartItem AS o WHERE o.eventCartItem = :eventCartItem", CartItem.class);
        q.setParameter("eventCartItem", eventCartItem);
        return q;
    }

    public static TypedQuery<CartItem> findCartItemsByEventCartItem(EventCartItem eventCartItem, String sortFieldName, String sortOrder) {
        if (eventCartItem == null)
            throw new IllegalArgumentException("The eventCartItem argument is required");
        EntityManager em = CartItem.entityManager();
        String jpaQuery = "SELECT o FROM CartItem AS o WHERE o.eventCartItem = :eventCartItem";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<CartItem> q = em.createQuery(jpaQuery, CartItem.class);
        q.setParameter("eventCartItem", eventCartItem);
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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static CartItem fromJsonToCartItem(String json) {
        return new JSONDeserializer<CartItem>().use(null, CartItem.class).deserialize(json);
    }

    public static String toJsonArray(Collection<CartItem> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<CartItem> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<CartItem> fromJsonArrayToCartItems(String json) {
        return new JSONDeserializer<List<CartItem>>().use("values", CartItem.class).deserialize(json);
    }
}
