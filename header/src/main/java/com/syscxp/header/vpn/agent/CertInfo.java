package com.syscxp.header.vpn.agent;

import com.syscxp.header.vpn.vpn.VpnCertVO;

public class CertInfo {
    private String accountUuid;
    private String caCert;
    private String clientCert;
    private String clientKey;
    private String serverCert;
    private String serverKey;
    private String clientConf;

    public VpnCertVO toVO(VpnCertVO vo){
        vo.setAccountUuid(this.accountUuid);
        vo.setCaCert(this.caCert);
        vo.setClientCert(this.clientCert);
        vo.setClientKey(this.clientKey);
        vo.setServerCert(this.serverCert);
        vo.setServerKey(this.serverKey);
        vo.setClientConf(this.clientConf);
        return vo;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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

    public String getCaCert() {
        return caCert;
    }

    public void setCaCert(String caCert) {
        this.caCert = caCert;
    }

    public String getClientConf() {
        return clientConf;
    }

    public void setClientConf(String clientConf) {
        this.clientConf = clientConf;
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
}
