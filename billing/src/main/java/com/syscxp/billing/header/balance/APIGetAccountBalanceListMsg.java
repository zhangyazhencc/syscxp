package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

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
