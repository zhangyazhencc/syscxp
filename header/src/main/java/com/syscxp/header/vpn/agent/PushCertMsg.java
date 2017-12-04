package com.syscxp.header.vpn.agent;

public class PushCertMsg extends VpnMessage {
    private String accountUuid;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
