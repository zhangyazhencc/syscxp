package com.syscxp.header.vpn.vpn;

import com.syscxp.header.search.TriggerIndex;
import com.syscxp.header.vpn.host.VpnHostVO;

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
    private String vpnUuid;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vpnUuid", insertable = false, updatable = false)
    private VpnVO vpn;
    @Column
    private String caCert;
    @Column
    private String clientCert;
    @Column
    private String clientKey;
    @Column
    private String clientConf;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;
    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVpnUuid() {
        return vpnUuid;
    }

    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }

    public VpnVO getVpn() {
        return vpn;
    }

    public void setVpn(VpnVO vpn) {
        this.vpn = vpn;
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
