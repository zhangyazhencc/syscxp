package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.BaseVO;

import javax.persistence.*;
import java.sql.Timestamp;

@Table
@Entity
public class ResourcePolicyRefVO{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String resourceUuid;

    @Column
    private String policyUuid;

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    @PreUpdate
    void preUpdate() {
        lastOpDate = null;
    }
}
