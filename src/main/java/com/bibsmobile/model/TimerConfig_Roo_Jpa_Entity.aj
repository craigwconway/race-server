// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.TimerConfig;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect TimerConfig_Roo_Jpa_Entity {
    
    declare @type: TimerConfig: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long TimerConfig.id;
    
    @Version
    @Column(name = "version")
    private Integer TimerConfig.version;
    
    public Long TimerConfig.getId() {
        return this.id;
    }
    
    public void TimerConfig.setId(Long id) {
        this.id = id;
    }
    
    public Integer TimerConfig.getVersion() {
        return this.version;
    }
    
    public void TimerConfig.setVersion(Integer version) {
        this.version = version;
    }
    
}
