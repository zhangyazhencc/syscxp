package com.syscxp.header.vpn.vpn;

import com.syscxp.header.search.TriggerIndex;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@TriggerIndex
public class VpnCertVO {

    @Id
    @Column
    private String uuid;
    @Column
    private String name;
    @Column
    private String accountUuid;
    @Column
    private String description;
    @Column
    private String caCert;
    @Column
    private String caKey;
    @Column
    private String clientCert;
    @Column
    private String clientKey;
    @Column
    private String serverCert;
    @Column
    private String serverKey;
    @Column
    private String dh1024Pem;
    @Column
    private Integer vpnNum;
    @Column
    private Integer version;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public Integer getVpnNum() {
        return vpnNum;
    }

    public void setVpnNum(Integer vpnNum) {
        this.vpnNum = vpnNum;
    }

    public String getDh1024Pem() {
        return dh1024Pem;
    }

    public void setDh1024Pem(String dh1024Pem) {
        this.dh1024Pem = dh1024Pem;
    }

    public String getCaKey() {
        return caKey;
    }

    public void setCaKey(String caKey) {
        this.caKey = caKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerCert() {
        return serverCert;
    }

    public void setServerCert(String serverCert) {
        this.serverCert = serverCert;
    }

    public String getServerKey() {
        return serverKey;
    }

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCaCert() {
        return caCert;
    }

    public void setCaCert(String caCert) {
        this.caCert = caCert;
    }

    public String getClientCert() {
        return clientCert;
    }

    public void setClientCert(String clientCert) {
        this.clientCert = clientCert;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
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
