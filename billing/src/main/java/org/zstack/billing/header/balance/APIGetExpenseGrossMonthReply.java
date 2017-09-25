package org.zstack.billing.header.balance;

import org.zstack.header.message.APIReply;
import org.zstack.header.rest.RestResponse;

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
