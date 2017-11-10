package com.syscxp.header.tunnel.aliEdgeRouter;

public class AliEdgeRouterInformationInventory {

//    private String name;
//    private String vbrUuid;
    private String accessPoint;
    private String status;
//    private String description;
//    private String physicalLineUuid;
    private String physicalLineOwerUuid;
    private String localGatewayIp;
    private String peerGatewayIp;
    private String peeringSubnetMask;
//    private Integer vlan;
//    private String createDate;

    public String getAccessPoint() {
        return accessPoint;
    }

    public void setAccessPoint(String accessPoint) {
        this.accessPoint = accessPoint;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhysicalLineOwerUuid() {
        return physicalLineOwerUuid;
    }

    public void setPhysicalLineOwerUuid(String physicalLineOwerUuid) {
        this.physicalLineOwerUuid = physicalLineOwerUuid;
    }

    public String getLocalGatewayIp() {
        return localGatewayIp;
    }

    public void setLocalGatewayIp(String localGatewayIp) {
        this.localGatewayIp = localGatewayIp;
    }

    public String getPeerGatewayIp() {
        return peerGatewayIp;
    }

    public void setPeerGatewayIp(String peerGatewayIp) {
        this.peerGatewayIp = peerGatewayIp;
    }

    public String getPeeringSubnetMask() {
        return peeringSubnetMask;
    }

    public void setPeeringSubnetMask(String peeringSubnetMask) {
        this.peeringSubnetMask = peeringSubnetMask;
    }

}
