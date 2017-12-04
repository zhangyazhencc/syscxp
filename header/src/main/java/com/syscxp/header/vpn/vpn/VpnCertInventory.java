package com.syscxp.header.vpn.vpn;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnCertVO.class)
public class VpnCertInventory {
    private String uuid;
    private String name;
    private String accountUuid;
    private String caCert;
    private String clientCert;
    private String clientKey;
    private String serverCert;
    private String serverKey;
    private String clientConf;
    private Integer version;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnCertInventory valueOf(VpnCertVO vo) {
        VpnCertInventory inv = new VpnCertInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setCaCert(vo.getCaCert());
        inv.setServerCert(vo.getServerCert());
        inv.setServerKey(vo.getServerKey());
        inv.setClientCert(vo.getClientCert());
        inv.setClientConf(vo.getClientConf());
        inv.setClientKey(vo.getClientKey());
        inv.setVersion(vo.getVersion());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<VpnCertInventory> valueOf(Collection<VpnCertVO> vos) {
        List<VpnCertInventory> invs = new ArrayList<>();
        for (VpnCertVO vo : vos) {
            invs.add(VpnCertInventory.valueOf(vo));
        }

        return invs;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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
