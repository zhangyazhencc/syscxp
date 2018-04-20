package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.search.TriggerIndex;
import com.syscxp.header.vpn.VpnAO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
@TriggerIndex
public class L3VpnVO extends VpnAO {

    @Column
    private String endpointUuid;
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
    @Column
    private String remoteIp;
    @Column
    private String monitorIp;
    public L3VpnVO() {
    }

    public L3VpnVO(L3VpnVO other) {
        super(other);
        this.endpointUuid = other.endpointUuid;
        this.l3NetworkUuid = other.l3NetworkUuid;
        this.workMode = other.workMode;
        this.startIp = other.startIp;
        this.stopIp = other.stopIp;
        this.netmask = other.netmask;
        this.gateway = other.gateway;
        this.remoteIp = other.remoteIp;
        this.monitorIp = other.monitorIp;
    }
    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getL3EndpointUuid() { return l3EndpointUuid; }

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

    public String getRemoteIp() { return remoteIp; }

    public void setRemoteIp(String remoteIp) { this.remoteIp = remoteIp; }

    public String getMonitorIp() { return monitorIp; }

    public void setMonitorIp(String monitorIp) { this.monitorIp = monitorIp; }
}
