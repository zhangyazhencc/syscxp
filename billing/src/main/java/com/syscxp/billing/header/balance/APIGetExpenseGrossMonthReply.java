package com.syscxp.billing.header.balance;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetExpenseGrossMonthReply extends APIReply {

    private List<ExpenseGross> inventories;

    public List<ExpenseGross> getInventories() {
        return inventories;
    }

    public void setInventories(List<ExpenseGross> inventories) {
        this.inventories = inventories;
    }


}
