package org.zstack.billing.header.balance;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIGetAccountBalanceMsg extends APISyncCallMessage {

    @APIParam(required = false,resourceType = AccountBalanceVO.class)
    private String accountUuid;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
