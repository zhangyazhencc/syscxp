package com.syscxp.header.vpn.vpn;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnCertVO.class)
public class VpnCertInventory {
    private String uuid;
    private String vpnUuid;
    private String caCert;
    private String clientCert;
    private String clientKey;
    private String clientConf;
    private VpnInventory vpnInventory;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnCertInventory valueOf(VpnCertVO vo) {
        VpnCertInventory inv = new VpnCertInventory();
        inv.setUuid(vo.getUuid());
        inv.setVpnUuid(vo.getVpnUuid());
        inv.setCaCert(vo.getCaCert());
        inv.setClientCert(vo.getClientCert());
        inv.setClientConf(vo.getClientConf());
        inv.setClientKey(vo.getClientKey());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setVpnInventory(VpnInventory.valueOf(vo.getVpn()));
        return inv;
    }

    public static List<VpnCertInventory> valueOf(Collection<VpnCertVO> vos) {
        List<VpnCertInventory> invs = new ArrayList<>();
        for (VpnCertVO vo : vos) {
            invs.add(VpnCertInventory.valueOf(vo));
        }

        return invs;
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

    public VpnInventory getVpnInventory() {
        return vpnInventory;
    }

    public void setVpnInventory(VpnInventory vpnInventory) {
        this.vpnInventory = vpnInventory;
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
