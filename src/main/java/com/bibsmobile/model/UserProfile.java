package com.bibsmobile.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Configurable
@Entity
@SuppressWarnings("serial")
public class UserProfile implements UserDetails {

    private String firstname;

    private String lastname;

    private String city;

    private String state;

    @Temporal(TemporalType.DATE)
    private Date birthdate;

    private String gender;

    @Email
    private String email;

    private String image;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userProfile", cascade = CascadeType.ALL)
    private Set<UserAuthorities> userAuthorities;

    private String username;

    private String password;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    private boolean enabled = true;

    private String facebookId;

    private String twitterId;

    private String googleId;

    @OneToMany(mappedBy = "userProfile")
    private Set<RaceResult> raceResults;
    
    @ManyToMany
    private List<UserProfile> friends = null;

    @ManyToMany
    @JoinTable(name="user_profile_friend_requests")
    private Set<UserProfile> friendRequests;
    
    @ManyToMany
    private Set<Event> events = null;

    private String phone;

    private String addressLine1;

    private String addressLine2;

    private String zipCode;

    private String emergencyContactName;

    private String emergencyContactPhone;

    private String hearFrom;
    
    private int zapposPoints = 0;

    private String dropboxId;

    private String dropboxAccessToken;

    private String stripeCustomerId;
    
    @OneToMany(mappedBy="user")
    private Set<UserBadge> badges;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "importUser")
    private Set<ResultsFile> resultsFiles;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "userProfile")
    private CartItem cartItem;

    @Override
    @JSON(include = false)
    public Set<UserAuthority> getAuthorities() {
        Set<UserAuthority> authorities = new HashSet<>();
        for (UserAuthorities uas : this.userAuthorities) {
            authorities.add(uas.getId().getUserAuthority());
        }
        return authorities;
    }

    @JSON(include = false)
    public List<UserAuthority> getNotAddedAuthorities() {
        List<UserAuthority> notAddedAuthorities = new ArrayList<>();
        List<UserAuthority> allAuthorities = UserAuthority.findAllUserAuthoritys();
        List<UserAuthority> assignedAuthorities = new ArrayList<>(this.getAuthorities());
        for (UserAuthority availableAuthority : allAuthorities) {
            boolean exists = false;
            for (UserAuthority assignedAuthority : assignedAuthorities) {
                if (availableAuthority.getId().equals(assignedAuthority.getId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                notAddedAuthorities.add(availableAuthority);
            }
        }
        return notAddedAuthorities;
    }

    @Override
    @JSON(include = false)
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public String toString() {
        return this.username;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").deepSerialize(this);
    }

    public String getFullName() {
        return new StringBuilder(StringUtils.isEmpty(this.firstname) ? StringUtils.EMPTY : this.firstname).append(" ")
                .append(StringUtils.isEmpty(this.lastname) ? StringUtils.EMPTY : this.lastname).toString();
    }

    /**
     */
    private String forgotPasswordCode;

    public static Long countFindUserProfilesByEmailEquals(String email) {
        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("The email argument is required");
        EntityManager em = UserProfile.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserProfile AS o WHERE o.email = :email AND o.username IS NOT NULL", Long.class);
        q.setParameter("email", email);
        return q.getSingleResult();
    }
    
    public static Long countFindEnabledUserProfilesByEmailEquals(String email) {
        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("The email argument is required");
        EntityManager em = UserProfile.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserProfile AS o WHERE o.email = :email AND o.enabled = 1", Long.class);
        q.setParameter("email", email);
        return q.getSingleResult();
    }

    public static TypedQuery<UserProfile> findUserProfilesByEmailEquals(String email) {
        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("The email argument is required");
        EntityManager em = UserProfile.entityManager();
        TypedQuery<UserProfile> q = em.createQuery("SELECT o FROM UserProfile AS o WHERE o.email = :email AND o.username IS NOT NULL", UserProfile.class);
        q.setParameter("email", email);
        return q;
    }

    public static UserProfile fromJsonToUserProfile(String json) {
        return new JSONDeserializer<UserProfile>().use(null, UserProfile.class).deserialize(json);
    }

    public static String toJsonArray(Collection<UserProfile> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<UserProfile> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<UserProfile> fromJsonArrayToUserProfiles(String json) {
        return new JSONDeserializer<List<UserProfile>>().use("values", UserProfile.class).deserialize(json);
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getAge() {
        return Years.yearsBetween(new DateTime(this.birthdate), new DateTime()).getYears();
    }

    public Date getBirthdate() {
        return this.birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<UserAuthorities> getUserAuthorities() {
        return this.userAuthorities;
    }

    public void setUserAuthorities(Set<UserAuthorities> userAuthorities) {
        this.userAuthorities = userAuthorities;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFacebookId() {
        return this.facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getTwitterId() {
        return this.twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public String getGoogleId() {
        return this.googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public Set<RaceResult> getRaceResults() {
        return this.raceResults;
    }

    public void setRaceResults(Set<RaceResult> raceResults) {
        this.raceResults = raceResults;
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

    public String getDropboxId() {
        return this.dropboxId;
    }

    public void setDropboxId(String dropboxId) {
        this.dropboxId = dropboxId;
    }

    public String getDropboxAccessToken() {
        return this.dropboxAccessToken;
    }

    public void setDropboxAccessToken(String dropboxAccessToken) {
        this.dropboxAccessToken = dropboxAccessToken;
    }

    public String getStripeCustomerId() {
        return this.stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public Set<ResultsFile> getResultsFiles() {
        return this.resultsFiles;
    }

    public void setResultsFiles(Set<ResultsFile> resultsFiles) {
        this.resultsFiles = resultsFiles;
    }

    public CartItem getCartItem() {
        return this.cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public String getForgotPasswordCode() {
        return this.forgotPasswordCode;
    }

    public void setForgotPasswordCode(String forgotPasswordCode) {
        this.forgotPasswordCode = forgotPasswordCode;
    }

    /**
	 * @return the friends
	 */
	public List<UserProfile> getFriends() {
		return friends;
	}

	/**
	 * @param friends the friends to set
	 */
	public void setFriends(List<UserProfile> friends) {
		this.friends = friends;
	}

	/**
	 * @return the friendRequests
	 */
	public Set<UserProfile> getFriendRequests() {
		return friendRequests;
	}

	/**
	 * @param friendRequests the friendRequests to set
	 */
	public void setFriendRequests(Set<UserProfile> friendRequests) {
		this.friendRequests = friendRequests;
	}

	/**
	 * @return the events
	 */
	public Set<Event> getEvents() {
		return events;
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(Set<Event> events) {
		this.events = events;
	}

	/**
	 * @return the badges
	 */
	public Set<UserBadge> getBadges() {
		return badges;
	}

	/**
	 * @param badges the badges to set
	 */
	public void setBadges(Set<UserBadge> badges) {
		this.badges = badges;
	}

	public static Long countFindUserProfilesByDropboxIdEquals(String dropboxId) {
        if (dropboxId == null || dropboxId.isEmpty())
            throw new IllegalArgumentException("The dropboxId argument is required");
        EntityManager em = UserProfile.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserProfile AS o WHERE o.dropboxId = :dropboxId", Long.class);
        q.setParameter("dropboxId", dropboxId);
        return q.getSingleResult();
    }

    public static Long countFindUserProfilesByForgotPasswordCodeEquals(String forgotPasswordCode) {
        if (forgotPasswordCode == null || forgotPasswordCode.isEmpty())
            throw new IllegalArgumentException("The forgotPasswordCode argument is required");
        EntityManager em = UserProfile.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserProfile AS o WHERE o.forgotPasswordCode = :forgotPasswordCode", Long.class);
        q.setParameter("forgotPasswordCode", forgotPasswordCode);
        return q.getSingleResult();
    }

    public static Long countFindUserProfilesByUsernameEquals(String username) {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("The username argument is required");
        EntityManager em = UserProfile.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserProfile AS o WHERE o.username = :username", Long.class);
        q.setParameter("username", username);
        return q.getSingleResult();
    }

    public static TypedQuery<UserProfile> findUserProfilesByDropboxIdEquals(String dropboxId) {
        if (dropboxId == null || dropboxId.isEmpty())
            throw new IllegalArgumentException("The dropboxId argument is required");
        EntityManager em = UserProfile.entityManager();
        TypedQuery<UserProfile> q = em.createQuery("SELECT o FROM UserProfile AS o WHERE o.dropboxId = :dropboxId", UserProfile.class);
        q.setParameter("dropboxId", dropboxId);
        return q;
    }

    public static TypedQuery<UserProfile> findUserProfilesByDropboxIdEquals(String dropboxId, String sortFieldName, String sortOrder) {
        if (dropboxId == null || dropboxId.isEmpty())
            throw new IllegalArgumentException("The dropboxId argument is required");
        EntityManager em = UserProfile.entityManager();
        String jpaQuery = "SELECT o FROM UserProfile AS o WHERE o.dropboxId = :dropboxId";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserProfile> q = em.createQuery(jpaQuery, UserProfile.class);
        q.setParameter("dropboxId", dropboxId);
        return q;
    }

    public static TypedQuery<UserProfile> findUserProfilesByEmailEquals(String email, String sortFieldName, String sortOrder) {
        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("The email argument is required");
        EntityManager em = UserProfile.entityManager();
        String jpaQuery = "SELECT o FROM UserProfile AS o WHERE o.email = :email";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserProfile> q = em.createQuery(jpaQuery, UserProfile.class);
        q.setParameter("email", email);
        return q;
    }

    public static TypedQuery<UserProfile> findUserProfilesByForgotPasswordCodeEquals(String forgotPasswordCode) {
        if (forgotPasswordCode == null || forgotPasswordCode.isEmpty())
            throw new IllegalArgumentException("The forgotPasswordCode argument is required");
        EntityManager em = UserProfile.entityManager();
        TypedQuery<UserProfile> q = em.createQuery("SELECT o FROM UserProfile AS o WHERE o.forgotPasswordCode = :forgotPasswordCode", UserProfile.class);
        q.setParameter("forgotPasswordCode", forgotPasswordCode);
        return q;
    }

    public static TypedQuery<UserProfile> findUserProfilesByForgotPasswordCodeEquals(String forgotPasswordCode, String sortFieldName, String sortOrder) {
        if (forgotPasswordCode == null || forgotPasswordCode.isEmpty())
            throw new IllegalArgumentException("The forgotPasswordCode argument is required");
        EntityManager em = UserProfile.entityManager();
        String jpaQuery = "SELECT o FROM UserProfile AS o WHERE o.forgotPasswordCode = :forgotPasswordCode";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserProfile> q = em.createQuery(jpaQuery, UserProfile.class);
        q.setParameter("forgotPasswordCode", forgotPasswordCode);
        return q;
    }

    public static TypedQuery<UserProfile> findUserProfilesByUsernameEquals(String username) {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("The username argument is required");
        EntityManager em = UserProfile.entityManager();
        TypedQuery<UserProfile> q = em.createQuery("SELECT o FROM UserProfile AS o WHERE o.username = :username", UserProfile.class);
        q.setParameter("username", username);
        return q;
    }
    
    public static TypedQuery<UserProfile> findEnabledUserProfilesByEmailEquals(String email) {
        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("The username argument is required");
        EntityManager em = UserProfile.entityManager();
        TypedQuery<UserProfile> q = em.createQuery("SELECT o FROM UserProfile AS o WHERE o.email = :email AND o.enabled = 1", UserProfile.class);
        q.setParameter("email", email);
        return q;
    }
    
    public static UserProfile findFriendRequestForUserById(UserProfile user, Long id) {
    	if (user == null || id == null) {
    		throw new IllegalArgumentException("The user or id is null");
    	}
    	EntityManager em = UserProfile.entityManager();
        TypedQuery<UserProfile> q = em.createQuery("SELECT o FROM UserProfile o inner join o.friendRequests WHERE o.id = :id", UserProfile.class);
        q.setParameter("id", id);
        return q.getSingleResult();
    }

    public static TypedQuery<UserProfile> findUserProfilesByUsernameEquals(String username, String sortFieldName, String sortOrder) {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("The username argument is required");
        EntityManager em = UserProfile.entityManager();
        String jpaQuery = "SELECT o FROM UserProfile AS o WHERE o.username = :username";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserProfile> q = em.createQuery(jpaQuery, UserProfile.class);
        q.setParameter("username", username);
        return q;
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("firstname", "lastname", "city", "state", "age", "birthdate", "gender", "email",
            "image", "userAuthorities", "username", "password", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "facebookId", "twitterId", "googleId",
            "raceResults", "phone", "addressLine1", "addressLine2", "zipCode", "emergencyContactName", "emergencyContactPhone", "hearFrom", "dropboxId", "dropboxAccessToken",
            "stripeCustomerId", "resultsFiles", "cartItem", "forgotPasswordCode");

    public static EntityManager entityManager() {
        EntityManager em = new UserProfile().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countUserProfiles() {
        return entityManager().createQuery("SELECT COUNT(o) FROM UserProfile o", Long.class).getSingleResult();
    }

    public static List<UserProfile> findAllUserProfiles() {
        return entityManager().createQuery("SELECT o FROM UserProfile o", UserProfile.class).getResultList();
    }

    public static List<UserProfile> findAllUserProfiles(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM UserProfile o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, UserProfile.class).getResultList();
    }

    public static UserProfile findUserProfile(Long id) {
        if (id == null)
            return null;
        return entityManager().find(UserProfile.class, id);
    }

    public static List<UserProfile> findUserProfileEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM UserProfile o", UserProfile.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<UserProfile> findUserProfileEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM UserProfile o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, UserProfile.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            UserProfile attached = UserProfile.findUserProfile(this.id);
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
    public UserProfile merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        UserProfile merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
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

	/**
	 * @return the zapposPoints
	 */
	public int getZapposPoints() {
		return zapposPoints;
	}

	/**
	 * @param zapposPoints the zapposPoints to set
	 */
	public void setZapposPoints(int zapposPoints) {
		this.zapposPoints = zapposPoints;
	}
}
