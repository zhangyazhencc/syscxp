package org.zstack.billing.header.bill;

import org.zstack.billing.APIQueryExpendMessage;
import org.zstack.header.billing.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
@AutoQuery(replyClass = APIQueryBillReply.class, inventoryClass = BillInventory.class)
public class APIQueryBillMsg extends APIQueryExpendMessage {

}
