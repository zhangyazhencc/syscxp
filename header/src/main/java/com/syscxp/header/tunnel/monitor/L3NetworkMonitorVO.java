package com.syscxp.header.tunnel.monitor;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import java.sql.Timestamp;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: 3层监控表.
 */
@Entity
public class L3NetworkMonitorVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String l3NetworkUuid;

    @Column
    private String srcL3EndpointUuid;

    @Column
    private String dstL3EndpointUuid;

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

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
    }

    public String getSrcL3EndpointUuid() {
        return srcL3EndpointUuid;
    }

    public void setSrcL3EndpointUuid(String srcL3EndpointUuid) {
        this.srcL3EndpointUuid = srcL3EndpointUuid;
    }

    public String getDstL3EndpointUuid() {
        return dstL3EndpointUuid;
    }

    public void setDstL3EndpointUuid(String dstL3EndpointUuid) {
        this.dstL3EndpointUuid = dstL3EndpointUuid;
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
