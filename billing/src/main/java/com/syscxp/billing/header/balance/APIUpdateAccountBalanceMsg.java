package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.math.BigDecimal;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"update"})
public class APIUpdateAccountBalanceMsg extends APIMessage {
    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam(required = false)
    private BigDecimal present;

    @APIParam(required = false)
    private BigDecimal credit;

    @APIParam(required = false)
    private BigDecimal cash;

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

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
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
}
