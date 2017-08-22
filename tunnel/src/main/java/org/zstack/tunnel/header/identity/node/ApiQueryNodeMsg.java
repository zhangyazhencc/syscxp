package org.zstack.tunnel.header.identity.node;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by DCY on 2017-08-21
 */
@AutoQuery(replyClass = ApiQueryNodeReply.class, inventoryClass = NodeInventory.class)
public class ApiQueryNodeMsg extends APIQueryMessage {
}
