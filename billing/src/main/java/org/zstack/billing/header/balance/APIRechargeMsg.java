package org.zstack.billing.header.balance;

import org.zstack.header.billing.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

import java.math.BigDecimal;

@Action(category = BillingConstant.ACTION_CATEGORY_RECHARGE)
public class APIRechargeMsg extends APISyncCallMessage {

    @APIParam(required = false)
    private String accountUuid;

    @APIParam(numberRange = {0,Long.MAX_VALUE})
    private BigDecimal total;

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
