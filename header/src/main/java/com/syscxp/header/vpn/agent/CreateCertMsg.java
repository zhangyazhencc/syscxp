package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.NeedReplyMessage;

public class CreateCertMsg extends NeedReplyMessage {
    private String vpnCertUuid;

    public String getVpnCertUuid() {
        return vpnCertUuid;
    }

    public void setVpnCertUuid(String vpnCertUuid) {
        this.vpnCertUuid = vpnCertUuid;
    }
}
