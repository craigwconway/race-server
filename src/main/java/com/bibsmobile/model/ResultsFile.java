package com.bibsmobile.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
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
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
public class ResultsFile {

    private String name;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "resultsFile")
	private Set<ResultsFileMapping> resultsFileMapping;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "resultsFile")
	private Set<ResultsImport> resultsImport;

    @NotNull
    private String contentType;
    @ManyToOne
    private Event event;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "SS")
    private Date created;

    @NotNull
    private long filesize;

    @NotNull
    @Size(max = 128)
    private String filePath;

    @Size(max = 42)
    private String sha1Checksum;

    @ManyToOne
    private UserProfile importUser;

    @Size(max = 128)
    private String dropboxPath;

    @NotNull
    private Boolean automaticUpdates;

    @Transient
    private byte[] content;

    @Override
    public String toString(){
        return this.name;
    }

    public ResultsImport getLatestImport() {
        ResultsImport latest = null;
        if (this.resultsImport == null) return null;
        for (ResultsImport ri : this.resultsImport) {
            if (latest == null || latest.getRunDate().compareTo(ri.getRunDate()) < 0) {
                latest = ri;
            }
        }
        return latest;
    }

    public ResultsFileMapping getLatestImportMapping() {
        ResultsImport latestImport = this.getLatestImport();
        if (latestImport == null) return null;
        return latestImport.getResultsFileMapping();
    }


	public static Long countFindResultsFilesByEvent(Event event) {
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        EntityManager em = ResultsFile.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM ResultsFile AS o WHERE o.event = :event", Long.class);
        q.setParameter("event", event);
        return q.getSingleResult();
    }

	public static Long countFindResultsFilesByNameEqualsAndEvent(String name, Event event) {
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        EntityManager em = ResultsFile.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM ResultsFile AS o WHERE o.name = :name  AND o.event = :event", Long.class);
        q.setParameter("name", name);
        q.setParameter("event", event);
        return q.getSingleResult();
    }

	public static TypedQuery<ResultsFile> findResultsFilesByEvent(Event event) {
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        EntityManager em = ResultsFile.entityManager();
        TypedQuery<ResultsFile> q = em.createQuery("SELECT o FROM ResultsFile AS o WHERE o.event = :event", ResultsFile.class);
        q.setParameter("event", event);
        return q;
    }

	public static TypedQuery<ResultsFile> findResultsFilesByEvent(Event event, String sortFieldName, String sortOrder) {
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        EntityManager em = ResultsFile.entityManager();
        String jpaQuery = "SELECT o FROM ResultsFile AS o WHERE o.event = :event";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<ResultsFile> q = em.createQuery(jpaQuery, ResultsFile.class);
        q.setParameter("event", event);
        return q;
    }

	public static TypedQuery<ResultsFile> findResultsFilesByNameEqualsAndEvent(String name, Event event) {
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        EntityManager em = ResultsFile.entityManager();
        TypedQuery<ResultsFile> q = em.createQuery("SELECT o FROM ResultsFile AS o WHERE o.name = :name  AND o.event = :event", ResultsFile.class);
        q.setParameter("name", name);
        q.setParameter("event", event);
        return q;
    }

	public static TypedQuery<ResultsFile> findResultsFilesByNameEqualsAndEvent(String name, Event event, String sortFieldName, String sortOrder) {
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        EntityManager em = ResultsFile.entityManager();
        String jpaQuery = "SELECT o FROM ResultsFile AS o WHERE o.name = :name  AND o.event = :event";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<ResultsFile> q = em.createQuery(jpaQuery, ResultsFile.class);
        q.setParameter("name", name);
        q.setParameter("event", event);
        return q;
    }

	public String getName() {
        return this.name;
    }

	public void setName(String name) {
        this.name = name;
    }

	public Set<ResultsFileMapping> getResultsFileMapping() {
        return this.resultsFileMapping;
    }

	public void setResultsFileMapping(Set<ResultsFileMapping> resultsFileMapping) {
        this.resultsFileMapping = resultsFileMapping;
    }

	public Set<ResultsImport> getResultsImport() {
        return this.resultsImport;
    }

	public void setResultsImport(Set<ResultsImport> resultsImport) {
        this.resultsImport = resultsImport;
    }

	public String getContentType() {
        return this.contentType;
    }

	public void setContentType(String contentType) {
        this.contentType = contentType;
    }

	public Event getEvent() {
        return this.event;
    }

	public void setEvent(Event event) {
        this.event = event;
    }

	public Date getCreated() {
        return this.created;
    }

	public void setCreated(Date created) {
        this.created = created;
    }

	public long getFilesize() {
        return this.filesize;
    }

	public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

	public String getFilePath() {
        return this.filePath;
    }

	public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

	public String getSha1Checksum() {
        return this.sha1Checksum;
    }

	public void setSha1Checksum(String sha1Checksum) {
        this.sha1Checksum = sha1Checksum;
    }

	public UserProfile getImportUser() {
        return this.importUser;
    }

	public void setImportUser(UserProfile importUser) {
        this.importUser = importUser;
    }

	public String getDropboxPath() {
        return this.dropboxPath;
    }

	public void setDropboxPath(String dropboxPath) {
        this.dropboxPath = dropboxPath;
    }

	public Boolean getAutomaticUpdates() {
        return this.automaticUpdates;
    }

	public void setAutomaticUpdates(Boolean automaticUpdates) {
        this.automaticUpdates = automaticUpdates;
    }

	public byte[] getContent() {
        return this.content;
    }

	public void setContent(byte[] content) {
        this.content = content;
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

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("name", "resultsFileMapping", "resultsImport", "contentType", "event", "created", "filesize", "filePath", "sha1Checksum", "importUser", "dropboxPath", "content");

	public static final EntityManager entityManager() {
        EntityManager em = new ResultsFile().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countResultsFiles() {
        return entityManager().createQuery("SELECT COUNT(o) FROM ResultsFile o", Long.class).getSingleResult();
    }

	public static List<ResultsFile> findAllResultsFiles() {
        return entityManager().createQuery("SELECT o FROM ResultsFile o", ResultsFile.class).getResultList();
    }

	public static List<ResultsFile> findAllResultsFiles(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM ResultsFile o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, ResultsFile.class).getResultList();
    }

	public static ResultsFile findResultsFile(Long id) {
        if (id == null) return null;
        return entityManager().find(ResultsFile.class, id);
    }

	public static List<ResultsFile> findResultsFileEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM ResultsFile o", ResultsFile.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	public static List<ResultsFile> findResultsFileEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM ResultsFile o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, ResultsFile.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            ResultsFile attached = ResultsFile.findResultsFile(this.id);
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
    public ResultsFile merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ResultsFile merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
}
