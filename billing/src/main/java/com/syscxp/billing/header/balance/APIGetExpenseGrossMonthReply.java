package com.syscxp.billing.header.balance;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventories")
public class APIGetExpenseGrossMonthReply extends APIReply {

    private List<ExpenseGross> inventories;

    public List<ExpenseGross> getInventories() {
        return inventories;
    }

    public void setInventories(List<ExpenseGross> inventories) {
        this.inventories = inventories;
    }


}
