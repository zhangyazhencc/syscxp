package org.zstack.billing.header.bill;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"read", "bill"})
@AutoQuery(replyClass = APIQueryBillReply.class, inventoryClass = BillInventory.class)
public class APIQueryBillMsg extends APIQueryMessage {

    @APIParam
    private boolean selfSelect;

    public boolean isSelfSelect() {
        return selfSelect;
    }

    public void setSelfSelect(boolean selfSelect) {
        this.selfSelect = selfSelect;
    }
}
