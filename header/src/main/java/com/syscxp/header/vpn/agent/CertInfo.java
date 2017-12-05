package com.syscxp.header.vpn.agent;

import com.syscxp.header.vpn.vpn.VpnCertVO;

public class CertInfo {
    public String ca_crt;
    public String ca_key;
    public String server_crt;
    public String server_key;
    public String dh1024_pem;

    public static CertInfo valueOf(VpnCertVO vo) {
        CertInfo certInfo = new CertInfo();
        certInfo.setCa_crt(vo.getCaCert());
        certInfo.setCa_key(vo.getCaKey());
        certInfo.setServer_crt(vo.getServerCert());
        certInfo.setServer_key(vo.getServerKey());
        certInfo.setDh1024_pem(vo.getDh1024Pem());
        return certInfo;
    }

    public String getCa_crt() {
        return ca_crt;
    }

    public void setCa_crt(String ca_crt) {
        this.ca_crt = ca_crt;
    }

    public String getCa_key() {
        return ca_key;
    }

    public void setCa_key(String ca_key) {
        this.ca_key = ca_key;
    }

    public String getServer_crt() {
        return server_crt;
    }

    public void setServer_crt(String server_crt) {
        this.server_crt = server_crt;
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
