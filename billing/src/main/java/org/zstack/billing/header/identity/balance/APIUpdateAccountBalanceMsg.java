package org.zstack.billing.header.identity.balance;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.math.BigDecimal;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"recharge"})
public class APIUpdateAccountBalanceMsg extends APIMessage {
    @APIParam(required = false)
    private String accountUuid;

    @APIParam(required = false)
    private BigDecimal presentBalance;

    @APIParam(required = false)
    private BigDecimal creditPoint;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public BigDecimal getPresentBalance() {
        return presentBalance;
    }

    public void setPresentBalance(BigDecimal presentBalance) {
        this.presentBalance = presentBalance;
    }

    public BigDecimal getCreditPoint() {
        return creditPoint;
    }

    public void setCreditPoint(BigDecimal creditPoint) {
        this.creditPoint = creditPoint;
    }

}
