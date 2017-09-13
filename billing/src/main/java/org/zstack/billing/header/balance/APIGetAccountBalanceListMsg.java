package org.zstack.billing.header.balance;

import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

import java.util.List;

public class APIGetAccountBalanceListMsg  extends APISyncCallMessage {

    @APIParam(nonempty = false)
    private List<String> accountUuids;

    public List<String> getAccountUuids() {
        return accountUuids;
    }

    public void setAccountUuids(List<String> accountUuids) {
        this.accountUuids = accountUuids;
    }
}
