package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountBalanceVO;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

import java.math.BigDecimal;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_PRESENT, names = {"update"}, adminOnly = true)
public class APIUpdateAccountPresentMsg extends APIMessage{

    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam
    private BigDecimal present;

    @APIParam(required = false)
    private String tradeNO;

    @APIParam(required = false)
    private String comment;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public BigDecimal getPresent() {
        return present;
    }

    public void setPresent(BigDecimal present) {
        this.present = present;
    }

    public String getTradeNO() {
        return tradeNO;
    }

    public void setTradeNO(String tradeNO) {
        this.tradeNO = tradeNO;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update AccountBalanceVO Present")
                        .resource(accountUuid, AccountBalanceVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
