package com.syscxp.billing.header.bill;

import com.syscxp.billing.header.balance.DealWay;

import java.math.BigDecimal;

public class BillStatistics {

    private String accountUuid;
    private DealWay dealWay;
    private BigDecimal expend;
    private BigDecimal income;

    public BillStatistics(){}
    public BillStatistics(Object[] objs) {
        accountUuid = (String) objs[0];
        dealWay = DealWay.valueOf((String) objs[1]);
        expend = (BigDecimal) objs[2];
        income = (BigDecimal) objs[3];
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
}
