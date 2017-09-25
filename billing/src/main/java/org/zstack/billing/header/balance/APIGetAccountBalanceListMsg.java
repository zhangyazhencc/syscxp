package org.zstack.billing.header.balance;

import org.zstack.header.billing.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

import java.util.List;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
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
