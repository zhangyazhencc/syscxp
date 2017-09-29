package org.zstack.vpn.header.vpn;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.util.Objects;

public class APIVpnMassage extends APIMessage {
    @APIParam(required = false)
    private String accountUuid;

    public String getAccountUuid() {
        if (accountUuid == null || accountUuid.equals(""))
            return getOpAccountUuid();
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getOpAccountUuid() {
        return getSession().getAccountUuid();
    }
}
