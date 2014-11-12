package com.bibsmobile.model;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Version;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Configurable
public class TimerConfig { 

	private int position;
	private String url; 
	private int readTimeout = 1;
	private int readPower;
	private int writePower;
	private int type; // Dummy, ThingMagic, etc 
	private String ports; // comma seperated list of ints 
	private int connectionTimeout;
	private String filename;
	static private int heartbeatTimeout = 5000;
	static private int heartbeats = 3;
	
	private enum Manufacturer {
		DEFAULT, THINGMAGIC
	}
	
	Manufacturer readerModel;
	
	public int getHeartbeats() {
		return heartbeats;
	}
	
	public int getHeartbeatTimeout() {
		return heartbeatTimeout;
	}
	
	public int getConnectionTimeout(){
		return connectionTimeout;
	}
	
	public String getFilename(){
		return filename;
	}
	 
	public void setReadTimeout(int t){ 
		if(t >= 1)
			readTimeout = t; 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ports == null) ? 0 : ports.hashCode());
		result = prime * result + readPower;
		result = prime * result + type;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + writePower;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimerConfig other = (TimerConfig) obj;
		if (ports == null) {
			if (other.ports != null)
				return false;
		} else if (!ports.equals(other.ports))
			return false;
		if (readPower != other.readPower)
			return false;
		if (type != other.type)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (writePower != other.writePower)
			return false;
		return true;
	}
	

	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }

	public static TimerConfig fromJsonToTimerConfig(String json) {
        return new JSONDeserializer<TimerConfig>()
        .use(null, TimerConfig.class).deserialize(json);
    }

	public static String toJsonArray(Collection<TimerConfig> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<TimerConfig> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<TimerConfig> fromJsonArrayToTimerConfigs(String json) {
        return new JSONDeserializer<List<TimerConfig>>()
        .use("values", TimerConfig.class).deserialize(json);
    }

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

	public int getPosition() {
        return this.position;
    }

	public void setPosition(int position) {
        this.position = position;
    }

	public String getUrl() {
        return this.url;
    }

	public void setUrl(String url) {
        this.url = url;
    }

	public int getReadTimeout() {
        return this.readTimeout;
    }

	public int getReadPower() {
        return this.readPower;
    }

	public void setReadPower(int readPower) {
        this.readPower = readPower;
    }

	public int getWritePower() {
        return this.writePower;
    }

	public void setWritePower(int writePower) {
        this.writePower = writePower;
    }

	public int getType() {
        return this.type;
    }

	public void setType(int type) {
        this.type = type;
    }

	public String getPorts() {
        return this.ports;
    }

	public void setPorts(String ports) {
        this.ports = ports;
    }

	public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

	public void setFilename(String filename) {
        this.filename = filename;
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("position", "url", "readTimeout", "readPower", "writePower", "type", "ports", "connectionTimeout", "filename");

	public static final EntityManager entityManager() {
        EntityManager em = new TimerConfig().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countTimerConfigs() {
        return entityManager().createQuery("SELECT COUNT(o) FROM TimerConfig o", Long.class).getSingleResult();
    }

	public static List<TimerConfig> findAllTimerConfigs() {
        return entityManager().createQuery("SELECT o FROM TimerConfig o", TimerConfig.class).getResultList();
    }

	public static List<TimerConfig> findAllTimerConfigs(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TimerConfig o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TimerConfig.class).getResultList();
    }

	public static TimerConfig findTimerConfig(Long id) {
        if (id == null) return null;
        return entityManager().find(TimerConfig.class, id);
    }

	public static List<TimerConfig> findTimerConfigEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM TimerConfig o", TimerConfig.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	public static List<TimerConfig> findTimerConfigEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TimerConfig o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TimerConfig.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

	@Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            TimerConfig attached = TimerConfig.findTimerConfig(this.id);
            this.entityManager.remove(attached);
        }
    }

	@Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

	@Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

	@Transactional
    public TimerConfig merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        TimerConfig merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
}
