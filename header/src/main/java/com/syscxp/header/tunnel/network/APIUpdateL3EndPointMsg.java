package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;


public class APIUpdateL3EndPointMsg extends APIMessage {

    @APIParam
    private String uuid;
    @APIParam(required = false)
    private Long bandwidth;
    @APIParam(required = false)
    private String routeType;
    @APIParam(required = false)
    private String status;
    @APIParam(required = false)
    private Long maxRouteNum;
    @APIParam(required = false)
    private String localIP;
    @APIParam(required = false)
    private String remoteIp;
    @APIParam(required = false)
    private String netmask;
    @APIParam(required = false)
    private String interfaceUuid;
    @APIParam(required = false)
    private String switchPortUuid;
    @APIParam(required = false)
    private Long vlan;
    @APIParam(required = false)
    private String rd;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Long getMaxRouteNum() {
        return maxRouteNum;
    }

    public void setMaxRouteNum(Long maxRouteNum) {
        this.maxRouteNum = maxRouteNum;
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

    public String getRd() {
        return rd;
    }

    public void setRd(String rd) {
        this.rd = rd;
    }
}
