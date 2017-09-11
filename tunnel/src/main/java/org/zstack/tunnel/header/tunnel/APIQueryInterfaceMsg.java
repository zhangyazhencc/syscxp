package org.zstack.tunnel.header.tunnel;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by DCY on 2017-09-11
 */
@AutoQuery(replyClass = APIQueryInterfaceReply.class, inventoryClass = InterfaceToSwitchPortInventory.class)
public class APIQueryInterfaceMsg extends APIQueryMessage {
}
