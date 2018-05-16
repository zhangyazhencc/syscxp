package com.syscxp.header.tunnel.tunnel;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/5/14
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class ExplicitPathVO {
    @Id
    @Column
    private String uuid;

    @Column
    private Integer traceSort;

    @Column
    private String switchName;

    @Column
    private String switchIP;

    @Column
    private String vsiTePathUuid;

    @Column
    private String tunnelsName;

    @Column
    private String explicitName;

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

    public Integer getTraceSort() {
        return traceSort;
    }

    public void setTraceSort(Integer traceSort) {
        this.traceSort = traceSort;
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

    public String getVsiTePathUuid() {
        return vsiTePathUuid;
    }

    public void setVsiTePathUuid(String vsiTePathUuid) {
        this.vsiTePathUuid = vsiTePathUuid;
    }

    public String getTunnelsName() {
        return tunnelsName;
    }

    public void setTunnelsName(String tunnelsName) {
        this.tunnelsName = tunnelsName;
    }

    public String getExplicitName() {
        return explicitName;
    }

    public void setExplicitName(String explicitName) {
        this.explicitName = explicitName;
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
