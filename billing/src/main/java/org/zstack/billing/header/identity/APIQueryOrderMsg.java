package org.zstack.billing.header.identity;

import org.springframework.util.StringUtils;
import org.zstack.header.message.APIParam;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

import java.util.ArrayList;
import java.util.List;

@AutoQuery(replyClass = APIQueryOrderReply.class, inventoryClass = OrderInventory.class)
public class APIQueryOrderMsg extends APIQueryMessage {

}
