package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;

public class CreateCertReply extends MessageReply {
    public  String vpnCert;

    public String getVpnCert() {
        return vpnCert;
    }

    public void setVpnCert(String vpnCert) {
        this.vpnCert = vpnCert;
    }
}
