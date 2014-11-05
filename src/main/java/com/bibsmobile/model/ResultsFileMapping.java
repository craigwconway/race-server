package com.bibsmobile.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

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
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.Version;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
@Configurable
public class ResultsFileMapping {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resultsFileMapping")
    private Set<ResultsImport> resultsImport;

    private String name;
    @ManyToOne
    private ResultsFile resultsFile;
    private boolean skipFirstRow;
    private String map;

    @Transient
    private List<String> row1 = new ArrayList<>();
    @Transient
    private List<String> row2 = new ArrayList<>();
    @Transient
    private NavigableMap<String, String> options = new TreeMap<>();

    @Override
    public String toString() {
        return this.map;
    }

    public String toJson() {
        return new JSONSerializer().include("*.row1", "*.row2", "*.options").exclude("*.class").serialize(this);
    }

    public List<String> getSelection() {
        // this pretents to spring as if a property with the name "selection"
        // exists
        if (this.map == null)
            return null;
        String[] mapSplits = this.map.split(",");
        return Arrays.asList(mapSplits);
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("resultsImport", "name", "resultsFile", "skipFirstRow", "map", "row1", "row2",
            "options");

    public static EntityManager entityManager() {
        EntityManager em = new ResultsFileMapping().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countResultsFileMappings() {
        return entityManager().createQuery("SELECT COUNT(o) FROM ResultsFileMapping o", Long.class).getSingleResult();
    }

    public static List<ResultsFileMapping> findAllResultsFileMappings() {
        return entityManager().createQuery("SELECT o FROM ResultsFileMapping o", ResultsFileMapping.class).getResultList();
    }

    public static List<ResultsFileMapping> findAllResultsFileMappings(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM ResultsFileMapping o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, ResultsFileMapping.class).getResultList();
    }

    public static ResultsFileMapping findResultsFileMapping(Long id) {
        if (id == null)
            return null;
        return entityManager().find(ResultsFileMapping.class, id);
    }

    public static List<ResultsFileMapping> findResultsFileMappingEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM ResultsFileMapping o", ResultsFileMapping.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<ResultsFileMapping> findResultsFileMappingEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM ResultsFileMapping o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, ResultsFileMapping.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            ResultsFileMapping attached = ResultsFileMapping.findResultsFileMapping(this.id);
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
    public ResultsFileMapping merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        ResultsFileMapping merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public Set<ResultsImport> getResultsImport() {
        return this.resultsImport;
    }

    public void setResultsImport(Set<ResultsImport> resultsImport) {
        this.resultsImport = resultsImport;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResultsFile getResultsFile() {
        return this.resultsFile;
    }

    public void setResultsFile(ResultsFile resultsFile) {
        this.resultsFile = resultsFile;
    }

    public boolean isSkipFirstRow() {
        return this.skipFirstRow;
    }

    public void setSkipFirstRow(boolean skipFirstRow) {
        this.skipFirstRow = skipFirstRow;
    }

    public String getMap() {
        return this.map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public List<String> getRow1() {
        return this.row1;
    }

    public void setRow1(List<String> row1) {
        this.row1 = row1;
    }

    public List<String> getRow2() {
        return this.row2;
    }

    public void setRow2(List<String> row2) {
        this.row2 = row2;
    }

    public NavigableMap<String, String> getOptions() {
        return this.options;
    }

    public void setOptions(NavigableMap<String, String> options) {
        this.options = options;
    }

    public static Long countFindResultsFileMappingsByResultsFile(ResultsFile resultsFile) {
        if (resultsFile == null)
            throw new IllegalArgumentException("The resultsFile argument is required");
        EntityManager em = ResultsFileMapping.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM ResultsFileMapping AS o WHERE o.resultsFile = :resultsFile", Long.class);
        q.setParameter("resultsFile", resultsFile);
        return q.getSingleResult();
    }

    public static TypedQuery<ResultsFileMapping> findResultsFileMappingsByResultsFile(ResultsFile resultsFile) {
        if (resultsFile == null)
            throw new IllegalArgumentException("The resultsFile argument is required");
        EntityManager em = ResultsFileMapping.entityManager();
        TypedQuery<ResultsFileMapping> q = em.createQuery("SELECT o FROM ResultsFileMapping AS o WHERE o.resultsFile = :resultsFile", ResultsFileMapping.class);
        q.setParameter("resultsFile", resultsFile);
        return q;
    }

    public static TypedQuery<ResultsFileMapping> findResultsFileMappingsByResultsFile(ResultsFile resultsFile, String sortFieldName, String sortOrder) {
        if (resultsFile == null)
            throw new IllegalArgumentException("The resultsFile argument is required");
        EntityManager em = ResultsFileMapping.entityManager();
        String jpaQuery = "SELECT o FROM ResultsFileMapping AS o WHERE o.resultsFile = :resultsFile";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<ResultsFileMapping> q = em.createQuery(jpaQuery, ResultsFileMapping.class);
        q.setParameter("resultsFile", resultsFile);
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

    public static ResultsFileMapping fromJsonToResultsFileMapping(String json) {
        return new JSONDeserializer<ResultsFileMapping>().use(null, ResultsFileMapping.class).deserialize(json);
    }

    public static String toJsonArray(Collection<ResultsFileMapping> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<ResultsFileMapping> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<ResultsFileMapping> fromJsonArrayToResultsFileMappings(String json) {
        return new JSONDeserializer<List<ResultsFileMapping>>().use("values", ResultsFileMapping.class).deserialize(json);
    }
}
