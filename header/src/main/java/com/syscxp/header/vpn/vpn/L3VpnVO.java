package com.syscxp.header.vpn.vpn;

import com.syscxp.header.vpn.VpnAO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table
@PrimaryKeyJoinColumn(name = "uuid", referencedColumnName = "uuid")
public class L3VpnVO extends VpnVO {

    @Column
    private String l3EndpointUuid;
    @Column
    private String workMode;
    @Column
    private String startIp;
    @Column
    private String endIp;
    public L3VpnVO() {
    }

    public L3VpnVO(L3VpnVO other) {
        super(other);
        this.l3EndpointUuid = other.l3EndpointUuid;
        this.workMode = other.workMode;
        this.startIp = other.startIp;
        this.endIp = other.endIp;
    }
    public String getL3EndpointUuid() { return l3EndpointUuid; }

    public void setL3EndpointUuid(String l3EndpointUuid) {
        this.l3EndpointUuid = l3EndpointUuid;
    }

    public String getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }

    public String getStartIp() {
        return startIp;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getEndIp() {
        return endIp;
    }

    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }


}
