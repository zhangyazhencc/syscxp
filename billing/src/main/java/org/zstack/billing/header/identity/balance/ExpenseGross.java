package org.zstack.billing.header.identity.balance;

import java.math.BigDecimal;

public class ExpenseGross {

    private String mon;
    private BigDecimal total;

    public ExpenseGross(){}
    public ExpenseGross(Object[] objs) {
        mon = (String) objs[0];
        total = (BigDecimal) objs[1];
    }

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
