package org.zstack.billing.header.balance;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryDealDetailReply.class, inventoryClass = DealDetailInventory.class)
public class APIQueryDealDetailMsg extends APIQueryMessage {

}
