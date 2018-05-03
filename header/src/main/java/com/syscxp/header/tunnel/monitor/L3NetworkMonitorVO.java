package com.syscxp.header.tunnel.monitor;


import com.syscxp.header.tunnel.network.L3EndpointVO;
import com.syscxp.header.tunnel.network.L3NetworkVO;

import javax.persistence.*;
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

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="l3NetworkUuid", insertable=false, updatable=false)
    private L3NetworkVO l3NetworkVO;

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="srcL3EndpointUuid", insertable=false, updatable=false)
    private L3EndpointVO srcEndpointVO;

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="dstL3EndpointUuid", insertable=false, updatable=false)
    private L3EndpointVO dstEndpointVO;

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

    public L3NetworkVO getL3NetworkVO() {
        return l3NetworkVO;
    }

    public void setL3NetworkVO(L3NetworkVO l3NetworkVO) {
        this.l3NetworkVO = l3NetworkVO;
    }

    public L3EndpointVO getSrcEndpointVO() {
        return srcEndpointVO;
    }

    public void setSrcEndpointVO(L3EndpointVO srcEndpointVO) {
        this.srcEndpointVO = srcEndpointVO;
    }

    public L3EndpointVO getDstEndpointVO() {
        return dstEndpointVO;
    }

    public void setDstEndpointVO(L3EndpointVO dstEndpointVO) {
        this.dstEndpointVO = dstEndpointVO;
    }
}
