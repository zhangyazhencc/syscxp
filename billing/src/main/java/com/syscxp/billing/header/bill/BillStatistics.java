package com.syscxp.billing.header.bill;

import com.syscxp.billing.header.balance.DealType;
import com.syscxp.billing.header.balance.DealWay;

import java.math.BigDecimal;

public class BillStatistics {

    private String accountUuid;
    private DealWay dealWay;
    private DealType type;
    private BigDecimal expend;
    private BigDecimal income;

    public BillStatistics(){}
    public BillStatistics(Object[] objs) {
        accountUuid = (String) objs[0];
        dealWay = DealWay.valueOf((String) objs[1]);
        type = DealType.valueOf((String) objs[2]);
        expend = (BigDecimal) objs[3];
        income = (BigDecimal) objs[4];
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public DealWay getDealWay() {
        return dealWay;
    }

    public void setDealWay(DealWay dealWay) {
        this.dealWay = dealWay;
    }

    public BigDecimal getExpend() {
        return expend;
    }

    public void setExpend(BigDecimal expend) {
        this.expend = expend;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public DealType getType() {
        return type;
    }

    public void setType(DealType type) {
        this.type = type;
    }
}
