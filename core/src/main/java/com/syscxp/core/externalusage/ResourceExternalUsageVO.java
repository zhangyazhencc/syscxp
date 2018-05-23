package com.syscxp.core.externalusage;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class ResourceExternalUsageVO {
    @Id
    @Column
    private String uuid;
    @Column
    private String resourceUuid;
    @Column
    private String resourceType;
    @Column
    private String usedFor;
    @Column
    private String usedForResourceUuid;
    @Column
    private String usedForResourceType;
    @Column
    @Enumerated(EnumType.STRING)
    private DeleteCascadeType deleteCascadeType;
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

    public String getUsedFor() {
        return usedFor;
    }

    public void setUsedFor(String usedFor) {
        this.usedFor = usedFor;
    }

    public String getUsedForResourceUuid() {
        return usedForResourceUuid;
    }

    public void setUsedForResourceUuid(String usedForResourceUuid) {
        this.usedForResourceUuid = usedForResourceUuid;
    }

    public String getUsedForResourceType() {
        return usedForResourceType;
    }

    public void setUsedForResourceType(String usedForResourceType) {
        this.usedForResourceType = usedForResourceType;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public DeleteCascadeType getDeleteCascadeType() {
        return deleteCascadeType;
    }

    public void setDeleteCascadeType(DeleteCascadeType deleteCascadeType) {
        this.deleteCascadeType = deleteCascadeType;
    }
}
