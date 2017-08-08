package org.zstack.billing.header.identity;

import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

public class APIGetAccountBalanceMsg extends APISyncCallMessage {

    @APIParam(nonempty = true)
    private String accountUuid;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
