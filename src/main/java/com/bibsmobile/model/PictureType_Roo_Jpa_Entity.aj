// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.PictureType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect PictureType_Roo_Jpa_Entity {
    
    declare @type: PictureType: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long PictureType.id;
    
    @Version
    @Column(name = "version")
    private Integer PictureType.version;
    
    public Long PictureType.getId() {
        return this.id;
    }
    
    public void PictureType.setId(Long id) {
        this.id = id;
    }
    
    public Integer PictureType.getVersion() {
        return this.version;
    }
    
    public void PictureType.setVersion(Integer version) {
        this.version = version;
    }
    
}