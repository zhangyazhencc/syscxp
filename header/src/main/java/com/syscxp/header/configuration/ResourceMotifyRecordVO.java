package com.syscxp.header.configuration;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/9/30
 */
@Entity
@Table
public class ResourceMotifyRecordVO {
    @Id
    @Column
    private String uuid;
    @Column
    private String resourceUuid;
    @Column
    private String resourceType;
    @Column
    private String opAccountUuid;
    @Column
    private String opUserUuid;
    @Column
    @Enumerated(EnumType.STRING)
    private MotifyType motifyType;
    @Column
    private Timestamp createDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getOpUserUuid() {
        return opUserUuid;
    }

    public void setOpUserUuid(String opUserUuid) {
        this.opUserUuid = opUserUuid;
    }

    public String getOpAccountUuid() {
        return opAccountUuid;
    }

    public void setOpAccountUuid(String opAccountUuid) {
        this.opAccountUuid = opAccountUuid;
    }

    public MotifyType getMotifyType() {
        return motifyType;
    }

    public void setMotifyType(MotifyType motifyType) {
        this.motifyType = motifyType;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
