package org.zstack.vpn.header.gateway;

import org.zstack.header.vo.ForeignKey;
import org.zstack.header.vo.ForeignKey.ReferenceOption;

import javax.persistence.*;
import java.sql.Timestamp;

public class TunnelIfaceVO {
    @Id
    @Column
    private String uuid;
    @Column
    @ForeignKey(parentEntityClass = VpnGatewayVO.class, parentKey = "uuid", onDeleteAction = ReferenceOption.CASCADE)
    private String gatewayUuid;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String tunnel;
    @Column
    private String serverIP;
    @Column
    private String clientIP;
    @Column
    private String mask;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="gatewayUuid", insertable=false, updatable=false)
    private VpnGatewayVO vpnGateway;

    public VpnGatewayVO getVpnGateway() {
        return vpnGateway;
    }

    public void setVpnGateway(VpnGatewayVO vpnGateway) {
        this.vpnGateway = vpnGateway;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getGatewayUuid() {
        return gatewayUuid;
    }

    public void setGatewayUuid(String gatewayUuid) {
        this.gatewayUuid = gatewayUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTunnel() {
        return tunnel;
    }

    public void setTunnel(String tunnel) {
        this.tunnel = tunnel;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
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
