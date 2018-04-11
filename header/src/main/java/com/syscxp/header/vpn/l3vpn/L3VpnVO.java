package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.search.TriggerIndex;
import com.syscxp.header.vpn.VpnAO;
import com.syscxp.header.vpn.host.VpnHostVO;
import com.syscxp.header.vpn.vpn.Payment;
import com.syscxp.header.vpn.vpn.VpnCertVO;
import com.syscxp.header.vpn.vpn.VpnState;
import com.syscxp.header.vpn.vpn.VpnStatus;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@TriggerIndex
public class L3VpnVO extends VpnAO {

    @Column
    private String l3EndpointUuid;
    @Column
    private String l3NetworkUuid;
    @Column
    private String workMode;
    @Column
    private String startIp;
    @Column
    private String stopIp;
    @Column
    private String netmask;
    @Column
    private String gateway;

    public L3VpnVO() {
    }

    public L3VpnVO(L3VpnVO other) {
        super(other);
        this.l3EndpointUuid = other.l3EndpointUuid;
        this.l3NetworkUuid = other.l3NetworkUuid;
        this.workMode = other.workMode;
        this.startIp = other.startIp;
        this.stopIp = other.stopIp;
        this.netmask = other.netmask;
        this.gateway = other.gateway;
    }

    public String getL3EndpointUuid() {
        return l3EndpointUuid;
    }

    public void setL3EndpointUuid(String l3EndpointUuid) {
        this.l3EndpointUuid = l3EndpointUuid;
    }

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
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

    public String getStopIp() {
        return stopIp;
    }

    public void setStopIp(String stopIp) {
        this.stopIp = stopIp;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }
}
