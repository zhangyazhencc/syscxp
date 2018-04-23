package com.syscxp.header.tunnel.network;

import com.syscxp.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/4/19
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class L3SlaveRouteVO {
    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = L3RouteVO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String l3RouteUuid;

    @Column
    private String routeIp;

    @Column
    private Integer preference;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getL3RouteUuid() {
        return l3RouteUuid;
    }

    public void setL3RouteUuid(String l3RouteUuid) {
        this.l3RouteUuid = l3RouteUuid;
    }

    public String getRouteIp() {
        return routeIp;
    }

    public void setRouteIp(String routeIp) {
        this.routeIp = routeIp;
    }

    public Integer getPreference() {
        return preference;
    }

    public void setPreference(Integer preference) {
        this.preference = preference;
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
