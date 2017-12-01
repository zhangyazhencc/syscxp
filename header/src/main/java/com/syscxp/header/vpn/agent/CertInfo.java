package com.syscxp.header.vpn.agent;

import com.syscxp.header.vpn.vpn.VpnCertVO;

public class CertInfo {
    private String caCert;
    private String clientCert;
    private String clientKey;
    private String clientConf;

    public VpnCertVO toVO(VpnCertVO vo){
        vo.setCaCert(this.caCert);
        vo.setClientCert(this.clientCert);
        vo.setClientKey(this.clientKey);
        vo.setClientConf(this.clientConf);
        return vo;
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
}
