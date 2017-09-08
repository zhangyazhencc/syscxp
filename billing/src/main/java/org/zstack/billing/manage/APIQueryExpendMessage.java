package org.zstack.billing.manage;

import org.zstack.header.message.APIParam;
import org.zstack.header.query.APIQueryMessage;

public class APIQueryExpendMessage extends APIQueryMessage {

    private String  accountUuid;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
