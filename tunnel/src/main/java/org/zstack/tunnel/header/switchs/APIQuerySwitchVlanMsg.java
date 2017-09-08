package org.zstack.tunnel.header.switchs;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by DCY on 2017-09-01
 */
@AutoQuery(replyClass = APIQuerySwitchVlanReply.class, inventoryClass = SwitchVlanToModelInventory.class)
public class APIQuerySwitchVlanMsg extends APIQueryMessage {
}
