package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.search.TriggerIndex;
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
public class L3VpnVO {

    @Id
    @Column
    private String uuid;
    @Column
    private String accountUuid;
    @Column
    private String hostUuid;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hostUuid", insertable = false, updatable = false)
    private VpnHostVO vpnHost;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vpnCertUuid", insertable = false, updatable = false)
    private VpnCertVO vpnCert;
    @Column
    private String vpnCertUuid;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String bandwidthOfferingUuid;
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
    private Integer port;
    @Column
    private Integer vlan;
    @Column
    @Enumerated(EnumType.STRING)
    private VpnState state;
    @Column
    @Enumerated(EnumType.STRING)
    private VpnStatus status;
    @Column
    private Integer duration;
    @Column
    private String clientConf;
    @Column
    private String secretKey;
    @Column
    private String secretId;
    @Column
    @Enumerated(EnumType.STRING)
    private Payment payment;
    @Column
    private Integer maxModifies;
    @Column
    private Timestamp expireDate;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getClientConf() {
        return clientConf;
    }

    public void setClientConf(String clientConf) {
        this.clientConf = clientConf;
    }

    public VpnCertVO getVpnCert() {
        return vpnCert;
    }

    public void setVpnCert(VpnCertVO vpnCert) {
        this.vpnCert = vpnCert;
    }

    public String getVpnCertUuid() {
        return vpnCertUuid;
    }

    public void setVpnCertUuid(String vpnCertUuid) {
        this.vpnCertUuid = vpnCertUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public VpnHostVO getVpnHost() {
        return vpnHost;
    }

    public void setVpnHost(VpnHostVO vpnHost) {
        this.vpnHost = vpnHost;
    }

    public Integer getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Integer maxModifies) {
        this.maxModifies = maxModifies;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public VpnState getState() {
        return state;
    }

    public void setState(VpnState state) {
        this.state = state;
    }

    public void setStatus(VpnStatus status) {
        this.status = status;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getL3EndpointUuid() {
        return l3EndpointUuid;
    }

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

    public VpnStatus getStatus() {
        return status;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
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

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }
}
