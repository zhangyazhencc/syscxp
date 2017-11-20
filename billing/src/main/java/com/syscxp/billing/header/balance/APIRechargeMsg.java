package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.math.BigDecimal;

@Action(category = BillingConstant.ACTION_CATEGORY_RECHARGE, names = "update")
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
