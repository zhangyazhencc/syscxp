package com.syscxp.header.vpn.agent;

import com.syscxp.header.vpn.vpn.VpnVO;

public class ClientConfInventory {
    public String caCert;
    public String clientCert;
    public String clientKey;
    public String clientConf;

    public static ClientConfInventory valueOf(VpnVO vo) {
        ClientConfInventory certInfo = new ClientConfInventory();
        certInfo.setCaCert(vo.getVpnCert().getCaCert());
        certInfo.setClientCert(vo.getVpnCert().getClientCert());
        certInfo.setClientKey(vo.getVpnCert().getClientKey());
        certInfo.setClientConf(vo.getClientConf());
        return certInfo;
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
}
