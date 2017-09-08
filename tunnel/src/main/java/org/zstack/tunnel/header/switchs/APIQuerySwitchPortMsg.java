package org.zstack.tunnel.header.switchs;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by DCY on 2017-09-01
 */
@AutoQuery(replyClass = APIQuerySwitchPortReply.class, inventoryClass = SwitchPortToModelInventory.class)
public class APIQuerySwitchPortMsg extends APIQueryMessage {
}
