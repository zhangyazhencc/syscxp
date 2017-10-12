package com.syscxp.tunnel.header.aliEdgeRouter;

import java.sql.Timestamp;

public class AliEdgeRouterInformationInventory {

    private String name;
    private String VbrUuid;
    private String AccessPoint;
    private String Status;
    private String description;
    private String physicalLineUuid;
    private String physicalLineOwerUuid;
    private String LocalGatewayIp;
    private String PeerGatewayIp;
    private String PeeringSubnetMask;
    private String vlan;
    private Timestamp createDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVbrUuid() {
        return VbrUuid;
    }

    public void setVbrUuid(String vbrUuid) {
        VbrUuid = vbrUuid;
    }

    public String getAccessPoint() {
        return AccessPoint;
    }

    public void setAccessPoint(String accessPoint) {
        AccessPoint = accessPoint;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhysicalLineUuid() {
        return physicalLineUuid;
    }

    public void setPhysicalLineUuid(String physicalLineUuid) {
        this.physicalLineUuid = physicalLineUuid;
    }

    public String getPhysicalLineOwerUuid() {
        return physicalLineOwerUuid;
    }

    public void setPhysicalLineOwerUuid(String physicalLineOwerUuid) {
        this.physicalLineOwerUuid = physicalLineOwerUuid;
    }

    public String getLocalGatewayIp() {
        return LocalGatewayIp;
    }

    public void setLocalGatewayIp(String localGatewayIp) {
        LocalGatewayIp = localGatewayIp;
    }

    public String getPeerGatewayIp() {
        return PeerGatewayIp;
    }

    public void setPeerGatewayIp(String peerGatewayIp) {
        PeerGatewayIp = peerGatewayIp;
    }

    public String getPeeringSubnetMask() {
        return PeeringSubnetMask;
    }

    public void setPeeringSubnetMask(String peeringSubnetMask) {
        PeeringSubnetMask = peeringSubnetMask;
    }

    public String getVlan() {
        return vlan;
    }

    public void setVlan(String vlan) {
        this.vlan = vlan;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
