package com.syscxp.header.vpn.agent;

import com.syscxp.header.vpn.vpn.VpnCertVO;

public class CertInfo {
    public String ca_ert;
    public String ca_key;
    public String server_cert;
    public String server_key;
    public String dh1024_pem;

    public static CertInfo valueOf(VpnCertVO vo) {
        CertInfo certInfo = new CertInfo();
        certInfo.setCa_ert(vo.getCaCert());
        certInfo.setCa_key(vo.getCaKey());
        certInfo.setServer_cert(vo.getServerCert());
        certInfo.setServer_key(vo.getServerKey());
        certInfo.setDh1024_pem(vo.getDh1024Pem());
        return certInfo;
    }

    public String getCa_ert() {
        return ca_ert;
    }

    public void setCa_ert(String ca_ert) {
        this.ca_ert = ca_ert;
    }

    public String getCa_key() {
        return ca_key;
    }

    public void setCa_key(String ca_key) {
        this.ca_key = ca_key;
    }

    public String getServer_cert() {
        return server_cert;
    }

    public void setServer_cert(String server_cert) {
        this.server_cert = server_cert;
    }

    public String getServer_key() {
        return server_key;
    }

    public void setServer_key(String server_key) {
        this.server_key = server_key;
    }

    public String getDh1024_pem() {
        return dh1024_pem;
    }

    public void setDh1024_pem(String dh1024_pem) {
        this.dh1024_pem = dh1024_pem;
    }
}
