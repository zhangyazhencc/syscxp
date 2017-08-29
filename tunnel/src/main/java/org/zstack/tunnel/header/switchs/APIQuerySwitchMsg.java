package org.zstack.tunnel.header.switchs;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by DCY on 2017-08-29
 */
@AutoQuery(replyClass = APIQuerySwitchReply.class, inventoryClass = SwitchInventory.class)
public class APIQuerySwitchMsg extends APIQueryMessage {
}
