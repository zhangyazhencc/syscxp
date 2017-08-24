package org.zstack.billing.header.identity.bill;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryBillReply.class, inventoryClass = BillInventory.class)
public class APIQueryBillMsg extends APIQueryMessage {
}