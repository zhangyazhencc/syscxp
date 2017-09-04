package org.zstack.billing.header.balance;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.math.BigDecimal;

public class APIReChargeProxyMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = AccountBalanceVO.class)
    private String accountUuid;

    @APIParam(required = false)
    private BigDecimal present;

    @APIParam(required = false)
    private BigDecimal credit;

    @APIParam(required = false)
    private BigDecimal cash;

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
}
