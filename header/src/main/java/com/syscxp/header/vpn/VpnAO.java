package com.syscxp.header.vpn;

import com.syscxp.header.vpn.host.VpnHostVO;
import com.syscxp.header.vpn.vpn.Payment;
import com.syscxp.header.vpn.vpn.VpnCertVO;
import com.syscxp.header.vpn.vpn.VpnState;
import com.syscxp.header.vpn.vpn.VpnStatus;

import javax.persistence.*;
import java.sql.Timestamp;

@MappedSuperclass
public class VpnAO {

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

    public VpnAO() {
    }

    public VpnAO(VpnAO other) {
        this.uuid = other.uuid;
        this.accountUuid = other.accountUuid;
        this.hostUuid = other.hostUuid;
        this.vpnHost = other.vpnHost;
        this.vpnCert = other.vpnCert;
        this.vpnCertUuid = other.vpnCertUuid;
        this.name = other.name;
        this.description = other.description;
        this.bandwidthOfferingUuid = other.bandwidthOfferingUuid;
        this.port = other.port;
        this.vlan = other.vlan;
        this.state = other.state;
        this.status = other.status;
        this.duration = other.duration;
        this.clientConf = other.clientConf;
        this.secretKey = other.secretKey;
        this.secretId = other.secretId;
        this.payment = other.payment;
        this.maxModifies = other.maxModifies;
        this.expireDate = other.expireDate;
        this.createDate = other.createDate;
        this.lastOpDate = other.lastOpDate;
    }

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
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

    public void setSecretKey(String secertKey) {
        this.secretKey = secertKey;
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
