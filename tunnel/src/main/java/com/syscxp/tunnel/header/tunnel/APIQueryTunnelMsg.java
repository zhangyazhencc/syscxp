package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by DCY on 2017-09-17
 */
@AutoQuery(replyClass = APIQueryTunnelReply.class, inventoryClass = TunnelInventory.class)
public class APIQueryTunnelMsg  extends APIQueryMessage {
}
