package org.zstack.tunnel.header.tunnel;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by DCY on 2017-09-17
 */
@AutoQuery(replyClass = APIQueryTunnelReply.class, inventoryClass = TunnelToNetWorkAndSwitchPortInventory.class)
public class APIQueryTunnelMsg  extends APIQueryMessage {
}
