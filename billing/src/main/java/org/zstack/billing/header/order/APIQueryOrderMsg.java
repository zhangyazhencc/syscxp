package org.zstack.billing.header.order;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryOrderReply.class, inventoryClass = OrderInventory.class)
public class APIQueryOrderMsg extends APIQueryMessage {

}
