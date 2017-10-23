package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Created by DCY on 2017-09-17
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryTunnelReply.class, inventoryClass = TunnelInventory.class)
public class APIQueryTunnelMsg  extends APIQueryMessage {
}
