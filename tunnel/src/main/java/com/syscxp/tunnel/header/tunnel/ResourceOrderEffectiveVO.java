package com.syscxp.tunnel.header.tunnel;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/10/30
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class ResourceOrderEffectiveVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String resourceUuid;

    @Column
    private String resourceType;

    @Column
    private String orderUuid;

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

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
