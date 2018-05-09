package com.syscxp.header.tunnel.tunnel;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/5/9
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class TETraceVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String teConfigUuid;

    @Column
    private String switchName;

    @Column
    private String switchIP;

    @Column
    private Integer traceSort;

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

    public String getTeConfigUuid() {
        return teConfigUuid;
    }

    public void setTeConfigUuid(String teConfigUuid) {
        this.teConfigUuid = teConfigUuid;
    }

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public String getSwitchIP() {
        return switchIP;
    }

    public void setSwitchIP(String switchIP) {
        this.switchIP = switchIP;
    }

    public Integer getTraceSort() {
        return traceSort;
    }

    public void setTraceSort(Integer traceSort) {
        this.traceSort = traceSort;
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
