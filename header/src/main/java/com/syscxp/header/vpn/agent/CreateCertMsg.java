package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.NeedReplyMessage;

public class CreateCertMsg extends NeedReplyMessage {
    private String vpnCertUuid;
    private String accountUuid;

    public String getVpnCertUuid() {
        return vpnCertUuid;
    }

    public void setVpnCertUuid(String vpnCertUuid) {
        this.vpnCertUuid = vpnCertUuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
