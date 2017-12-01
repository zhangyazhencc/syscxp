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
    private String accountUuid;
    @Column
    private String caCert;
    @Column
    private String clientCert;
    @Column
    private String clientKey;
    @Column
    private String clientConf;
    @Column
    private Integer version;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;
    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
        version += 1;
    }

    @PrePersist
    private void prePersistVersion() {
        version = 0;
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

    public String getClientConf() {
        return clientConf;
    }

    public void setClientConf(String clientConf) {
        this.clientConf = clientConf;
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
