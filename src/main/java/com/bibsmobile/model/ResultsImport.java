package com.bibsmobile.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
public class ResultsImport {

    private Date runDate = new Date();
    @ManyToOne
    private ResultsFile resultsFile;
    @ManyToOne
    private ResultsFileMapping resultsFileMapping;

    private int rowsProcessed;
    private int errors;
    private String errorRows = new String();

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("runDate", "resultsFile", "resultsFileMapping", "rowsProcessed", "errors", "errorRows");

    public static EntityManager entityManager() {
        EntityManager em = new ResultsImport().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countResultsImports() {
        return entityManager().createQuery("SELECT COUNT(o) FROM ResultsImport o", Long.class).getSingleResult();
    }

    public static List<ResultsImport> findAllResultsImports() {
        return entityManager().createQuery("SELECT o FROM ResultsImport o", ResultsImport.class).getResultList();
    }

    public static List<ResultsImport> findAllResultsImports(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM ResultsImport o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, ResultsImport.class).getResultList();
    }

    public static ResultsImport findResultsImport(Long id) {
        if (id == null)
            return null;
        return entityManager().find(ResultsImport.class, id);
    }

    public static List<ResultsImport> findResultsImportEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM ResultsImport o", ResultsImport.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<ResultsImport> findResultsImportEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM ResultsImport o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, ResultsImport.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            ResultsImport attached = ResultsImport.findResultsImport(this.id);
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
    public ResultsImport merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        ResultsImport merged = this.entityManager.merge(this);
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

    public static Long countFindResultsImportsByResultsFile(ResultsFile resultsFile) {
        if (resultsFile == null)
            throw new IllegalArgumentException("The resultsFile argument is required");
        EntityManager em = ResultsImport.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM ResultsImport AS o WHERE o.resultsFile = :resultsFile", Long.class);
        q.setParameter("resultsFile", resultsFile);
        return q.getSingleResult();
    }

    public static TypedQuery<ResultsImport> findResultsImportsByResultsFile(ResultsFile resultsFile) {
        if (resultsFile == null)
            throw new IllegalArgumentException("The resultsFile argument is required");
        EntityManager em = ResultsImport.entityManager();
        TypedQuery<ResultsImport> q = em.createQuery("SELECT o FROM ResultsImport AS o WHERE o.resultsFile = :resultsFile", ResultsImport.class);
        q.setParameter("resultsFile", resultsFile);
        return q;
    }

    public static TypedQuery<ResultsImport> findResultsImportsByResultsFile(ResultsFile resultsFile, String sortFieldName, String sortOrder) {
        if (resultsFile == null)
            throw new IllegalArgumentException("The resultsFile argument is required");
        EntityManager em = ResultsImport.entityManager();
        String jpaQuery = "SELECT o FROM ResultsImport AS o WHERE o.resultsFile = :resultsFile";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<ResultsImport> q = em.createQuery(jpaQuery, ResultsImport.class);
        q.setParameter("resultsFile", resultsFile);
        return q;
    }

    public Date getRunDate() {
        return this.runDate;
    }

    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }

    public ResultsFile getResultsFile() {
        return this.resultsFile;
    }

    public void setResultsFile(ResultsFile resultsFile) {
        this.resultsFile = resultsFile;
    }

    public ResultsFileMapping getResultsFileMapping() {
        return this.resultsFileMapping;
    }

    public void setResultsFileMapping(ResultsFileMapping resultsFileMapping) {
        this.resultsFileMapping = resultsFileMapping;
    }

    public int getRowsProcessed() {
        return this.rowsProcessed;
    }

    public void setRowsProcessed(int rowsProcessed) {
        this.rowsProcessed = rowsProcessed;
    }

    public int getErrors() {
        return this.errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public String getErrorRows() {
        return this.errorRows;
    }

    public void setErrorRows(String errorRows) {
        this.errorRows = errorRows;
    }
}
