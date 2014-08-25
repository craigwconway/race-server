package com.bibsmobile.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;

import flexjson.JSONSerializer;

@RooJavaBean
@RooJpaActiveRecord(finders = { "findResultsFileMappingsByResultsFile" })
@RooJson
public class ResultsFileMapping {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resultsFileMapping")
    private Set<ResultsImport> resultsImport;

    private String name;
    @ManyToOne
    private ResultsFile resultsFile;
    private boolean skipFirstRow;
    private String map;

    @Transient
    private List<String> row1 = new ArrayList<String>();
    @Transient
    private List<String> row2 = new ArrayList<String>();
    @Transient
    private TreeMap<String, String> options = new TreeMap<String, String>();

    @Override
    public String toString() {
        return map;
    }

    public String toJson() {
        return new JSONSerializer().include("*.row1", "*.row2", "*.options")
        .exclude("*.class").serialize(this);
    }

    public String getMapForRow(int idx) {
        String mapSplits[] = this.map.split(",");
        if (idx < mapSplits.length) return mapSplits[idx];
        else return null;
    }

}
