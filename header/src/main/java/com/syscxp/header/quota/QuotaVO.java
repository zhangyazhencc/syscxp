package com.syscxp.header.quota;

import com.syscxp.header.vo.BaseResource;
import com.syscxp.header.vo.ResourceVO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by frank on 7/13/2015.
 */
@Entity
@Table
@BaseResource
public class QuotaVO extends ResourceVO {
    @Column
    private String name;

    @Column
    private String identityUuid;

    @Column
    private String identityType;

    @Column
    private long value;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentityUuid() {
        return identityUuid;
    }

    public void setIdentityUuid(String identityUuid) {
        this.identityUuid = identityUuid;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
