package com.bibsmobile.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
@Configurable
public class EventCartItem {

    @ManyToOne
    private Event event;

    private String name;

    private String description;

    private long price;

    private int available;

    private int purchased;

    private String eventType;

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
    private long donationAmount;

    /**
     */
    private String charityName;

    /**
     */
    private String tshirtSizes;

    /**
     */
    private String tshirtColors;

    /**
     */
    private String tshirtImageUrls;

    /**
     */
    private int minAge = 1;

    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, mappedBy = "eventCartItem")
    private Set<EventCartItemPriceChange> priceChanges;

    /**
     */
    private int maxAge = 120;

    /**
     */
    @Enumerated
    private EventCartItemGenderEnum gender = EventCartItemGenderEnum.MALE_AND_FEMALE;

    @Transient
    private Date birthDate;
    @Transient
    private String email;
    @Transient
    private String phone;
    @Transient
    private String addressLine1;
    @Transient
    private String addressLine2;
    @Transient
    private String zipCode;
    @Transient
    private String emergencyContactName;
    @Transient
    private String emergencyContactPhone;
    @Transient
    private String hearFrom;

    public long getActualPrice() {
        if (CollectionUtils.isEmpty(this.priceChanges)) {
            return this.price;
        }
        Date now = new Date();
        for (EventCartItemPriceChange priceChange : this.priceChanges) {
            if (priceChange.getStartDate() != null && priceChange.getEndDate() != null) {
                if (now.after(priceChange.getStartDate()) && now.before(priceChange.getEndDate())) {
                    return priceChange.getPrice();
                }
            }
        }
        return this.price;
    }

    public void addEmptyPriceChange() {
    	// For so to use in create.jspx
    	Set<EventCartItemPriceChange> currentPriceChanges = getPriceChanges();
    	EventCartItemPriceChange newPriceChange = new EventCartItemPriceChange();
    	currentPriceChanges.add(newPriceChange);
    	this.setPriceChanges(currentPriceChanges);
    }
    
    public static TypedQuery<EventCartItem> findEventCartItemsByEvents(List<Event> events) {
        if (events == null)
            throw new IllegalArgumentException("The events argument is required");
        EntityManager em = EventCartItem.entityManager();
        TypedQuery<EventCartItem> q = em.createQuery("SELECT o FROM EventCartItem AS o WHERE o.event IN (:events)", EventCartItem.class);
        q.setParameter("events", events);
        return q;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static EventCartItem fromJsonToEventCartItem(String json) {
        return new JSONDeserializer<EventCartItem>().use(null, EventCartItem.class).deserialize(json);
    }

    public static Collection<EventCartItem> fromJsonArrayToEventCartItems(String json) {
        return new JSONDeserializer<List<EventCartItem>>().use("values", EventCartItem.class).deserialize(json);
    }

    public static String toDeepJsonArray(Collection<EventCartItem> collection) {
    	return new JSONSerializer().exclude("event.awardCategorys")
    			.exclude("event.resultsFiles")
    			.exclude("event.raceResults")
    			.exclude("event.raceImages")
    			.exclude("event.eventUserGroups")
    			.exclude("event.results")
    			.exclude("event.maps")
    			.exclude("event.photos")	
    			.exclude("*.class").deepSerialize(collection);
    }
    
    public static String toJsonArray(Collection<EventCartItem> collection) {
        return new JSONSerializer().include("*.children").exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<EventCartItem> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj.getClass() != this.getClass()) return false;
        EventCartItem rhs = (EventCartItem) obj;
        return new EqualsBuilder().append(this.addressLine1, rhs.addressLine1).append(this.addressLine2, rhs.addressLine2).append(this.available, rhs.available)
                .append(this.birthDate, rhs.birthDate).append(this.charityName, rhs.charityName).append(this.description, rhs.description)
                .append(this.donationAmount, rhs.donationAmount).append(this.email, rhs.email).append(this.emergencyContactName, rhs.emergencyContactName)
                .append(this.emergencyContactPhone, rhs.emergencyContactPhone).append(this.event, rhs.event).append(this.eventType, rhs.eventType).append(this.gender, rhs.gender)
                .append(this.hearFrom, rhs.hearFrom).append(this.id, rhs.id).append(this.maxAge, rhs.maxAge).append(this.minAge, rhs.minAge).append(this.name, rhs.name)
                .append(this.phone, rhs.phone).append(this.price, rhs.price).append(this.purchased, rhs.purchased).append(this.timeEnd, rhs.timeEnd)
                .append(this.timeLimit, rhs.timeLimit).append(this.timeStart, rhs.timeStart).append(this.tshirtColors, rhs.tshirtColors)
                .append(this.tshirtImageUrls, rhs.tshirtImageUrls).append(this.tshirtSizes, rhs.tshirtSizes).append(this.type, rhs.type).append(this.zipCode, rhs.zipCode)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.addressLine1).append(this.addressLine2).append(this.available).append(this.birthDate).append(this.charityName).append(this.description).append(this.donationAmount).append(this.email)
                .append(this.emergencyContactName).append(this.emergencyContactPhone).append(this.event).append(this.eventType).append(this.gender).append(this.hearFrom)
                .append(this.id).append(this.maxAge).append(this.minAge).append(this.name).append(this.phone).append(this.price).append(this.purchased).append(this.timeEnd)
                .append(this.timeLimit).append(this.timeStart).append(this.tshirtColors).append(this.tshirtImageUrls).append(this.tshirtSizes).append(this.type)
                .append(this.zipCode).toHashCode();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return this.price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getAvailable() {
        return this.available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getPurchased() {
        return this.purchased;
    }

    public void setPurchased(int purchased) {
        this.purchased = purchased;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public boolean isTimeLimit() {
        return this.timeLimit;
    }

    public void setTimeLimit(boolean timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Date getTimeStart() {
        return this.timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return this.timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public EventCartItemTypeEnum getType() {
        return this.type;
    }

    public void setType(EventCartItemTypeEnum type) {
        this.type = type;
    }

    public long getDonationAmount() {
        return this.donationAmount;
    }

    public void setDonationAmount(long donationAmount) {
        this.donationAmount = donationAmount;
    }

    public String getCharityName() {
        return this.charityName;
    }

    public void setCharityName(String charityName) {
        this.charityName = charityName;
    }

    public String getTshirtSizes() {
        return this.tshirtSizes;
    }

    public void setTshirtSizes(String tshirtSizes) {
        this.tshirtSizes = tshirtSizes;
    }

    public String getTshirtColors() {
        return this.tshirtColors;
    }

    public void setTshirtColors(String tshirtColors) {
        this.tshirtColors = tshirtColors;
    }

    public String getTshirtImageUrls() {
        return this.tshirtImageUrls;
    }

    public void setTshirtImageUrls(String tshirtImageUrls) {
        this.tshirtImageUrls = tshirtImageUrls;
    }

    public int getMinAge() {
        return this.minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public Set<EventCartItemPriceChange> getPriceChanges() {
        return this.priceChanges;
    }

    public void setPriceChanges(Set<EventCartItemPriceChange> priceChanges) {
        this.priceChanges = priceChanges;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public EventCartItemGenderEnum getGender() {
        return this.gender;
    }

    public void setGender(EventCartItemGenderEnum gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getEmergencyContactName() {
        return this.emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return this.emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public String getHearFrom() {
        return this.hearFrom;
    }

    public void setHearFrom(String hearFrom) {
        this.hearFrom = hearFrom;
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("event", "name", "description", "price", "available", "purchased",
            "eventType", "timeLimit", "timeStart", "timeEnd", "type", "donationAmount", "charityName", "tshirtSizes",
            "tshirtColors", "tshirtImageUrls", "minAge", "priceChanges", "maxAge", "gender", "birthDate", "email", "phone", "addressLine1", "addressLine2", "zipCode",
            "emergencyContactName", "emergencyContactPhone", "hearFrom");

    public static EntityManager entityManager() {
        EntityManager em = new EventCartItem().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countEventCartItems() {
        return entityManager().createQuery("SELECT COUNT(o) FROM EventCartItem o", Long.class).getSingleResult();
    }

    public static List<EventCartItem> findAllEventCartItems() {
        return entityManager().createQuery("SELECT o FROM EventCartItem o", EventCartItem.class).getResultList();
    }

    public static List<EventCartItem> findAllEventCartItems(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventCartItem o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventCartItem.class).getResultList();
    }

    public static EventCartItem findEventCartItem(Long id) {
        if (id == null)
            return null;
        return entityManager().find(EventCartItem.class, id);
    }

    public static List<EventCartItem> findEventCartItemEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM EventCartItem o", EventCartItem.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<EventCartItem> findEventCartItemEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventCartItem o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventCartItem.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            EventCartItem attached = EventCartItem.findEventCartItem(this.id);
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
    public EventCartItem merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        EventCartItem merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static Long countFindEventCartItemsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventCartItem.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM EventCartItem AS o WHERE o.event = :event", Long.class);
        q.setParameter("event", event);
        return q.getSingleResult();
    }

    public static Long countFindEventCartItemsByNameEquals(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The name argument is required");
        EntityManager em = EventCartItem.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM EventCartItem AS o WHERE o.name = :name", Long.class);
        q.setParameter("name", name);
        return q.getSingleResult();
    }

    public static Long countFindEventCartItemsByType(EventCartItemTypeEnum type) {
        if (type == null)
            throw new IllegalArgumentException("The type argument is required");
        EntityManager em = EventCartItem.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM EventCartItem AS o WHERE o.type = :type", Long.class);
        q.setParameter("type", type);
        return q.getSingleResult();
    }

    public static TypedQuery<EventCartItem> findEventCartItemsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventCartItem.entityManager();
        TypedQuery<EventCartItem> q = em.createQuery("SELECT o FROM EventCartItem AS o WHERE o.event = :event", EventCartItem.class);
        q.setParameter("event", event);
        return q;
    }

    public static TypedQuery<EventCartItem> findEventCartItemsByEvent(Event event, String sortFieldName, String sortOrder) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventCartItem.entityManager();
        String jpaQuery = "SELECT o FROM EventCartItem AS o WHERE o.event = :event";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<EventCartItem> q = em.createQuery(jpaQuery, EventCartItem.class);
        q.setParameter("event", event);
        return q;
    }

    public static TypedQuery<EventCartItem> findEventCartItemsByNameEquals(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The name argument is required");
        EntityManager em = EventCartItem.entityManager();
        TypedQuery<EventCartItem> q = em.createQuery("SELECT o FROM EventCartItem AS o WHERE o.name = :name", EventCartItem.class);
        q.setParameter("name", name);
        return q;
    }

    public static TypedQuery<EventCartItem> findEventCartItemsByNameEquals(String name, String sortFieldName, String sortOrder) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The name argument is required");
        EntityManager em = EventCartItem.entityManager();
        String jpaQuery = "SELECT o FROM EventCartItem AS o WHERE o.name = :name";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<EventCartItem> q = em.createQuery(jpaQuery, EventCartItem.class);
        q.setParameter("name", name);
        return q;
    }

    public static TypedQuery<EventCartItem> findEventCartItemsByType(EventCartItemTypeEnum type) {
        if (type == null)
            throw new IllegalArgumentException("The type argument is required");
        EntityManager em = EventCartItem.entityManager();
        TypedQuery<EventCartItem> q = em.createQuery("SELECT o FROM EventCartItem AS o WHERE o.type = :type", EventCartItem.class);
        q.setParameter("type", type);
        return q;
    }

    public static TypedQuery<EventCartItem> findEventCartItemsByType(EventCartItemTypeEnum type, String sortFieldName, String sortOrder) {
        if (type == null)
            throw new IllegalArgumentException("The type argument is required");
        EntityManager em = EventCartItem.entityManager();
        String jpaQuery = "SELECT o FROM EventCartItem AS o WHERE o.type = :type";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<EventCartItem> q = em.createQuery(jpaQuery, EventCartItem.class);
        q.setParameter("type", type);
        return q;
    }
}
