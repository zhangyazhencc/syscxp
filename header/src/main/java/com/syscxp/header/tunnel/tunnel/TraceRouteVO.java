package com.syscxp.header.tunnel.tunnel;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/11/28
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class TraceRouteVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String tunnelUuid;

    @Column
    private Integer traceSort;

    @Column
    private String routeIP;

    @Column
    private String timesFirst;

    @Column
    private String timesSecond;

    @Column
    private String timesThird;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

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

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public Integer getTraceSort() {
        return traceSort;
    }

    public void setTraceSort(Integer traceSort) {
        this.traceSort = traceSort;
    }

    public String getRouteIP() {
        return routeIP;
    }

    public void setRouteIP(String routeIP) {
        this.routeIP = routeIP;
    }

    public String getTimesFirst() {
        return timesFirst;
    }

    public void setTimesFirst(String timesFirst) {
        this.timesFirst = timesFirst;
    }

    public String getTimesSecond() {
        return timesSecond;
    }

    public void setTimesSecond(String timesSecond) {
        this.timesSecond = timesSecond;
    }

    public String getTimesThird() {
        return timesThird;
    }

    public void setTimesThird(String timesThird) {
        this.timesThird = timesThird;
    }

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
}
