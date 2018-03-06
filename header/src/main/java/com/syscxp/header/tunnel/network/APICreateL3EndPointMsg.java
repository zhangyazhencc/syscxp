package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;


public class APICreateL3EndPointMsg extends APIMessage {

    @APIParam
    private String l3NetworkUuid;
    @APIParam
    private String endpointUuid;
    @APIParam
    private Long bandwidth;
    @APIParam(required = false)
    private String routeType;
    @APIParam(required = false)
    private String status;
    @APIParam(required = false)
    private String localIP;
    @APIParam(required = false)
    private String remoteIp;
    @APIParam(required = false)
    private String netmask;
    @APIParam
    private String interfaceUuid;
    @APIParam(required = false)
    private String switchPortUuid;
    @APIParam(required = false)
    private Long vlan;

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public Long getVlan() {
        return vlan;
    }

    public void setVlan(Long vlan) {
        this.vlan = vlan;
    }

}
