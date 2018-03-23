package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountBalanceVO;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

import java.math.BigDecimal;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_CREDIT, names = {"update"}, adminOnly = true)
public class APIUpdateAccountCreditMsg extends APIMessage {
    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam(required = false)
    private BigDecimal credit;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update AccountBalanceVO Credit")
                        .resource(accountUuid, AccountBalanceVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
