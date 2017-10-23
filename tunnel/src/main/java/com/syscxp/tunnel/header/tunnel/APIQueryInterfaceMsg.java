package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Created by DCY on 2017-09-11
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryInterfaceReply.class, inventoryClass = InterfaceInventory.class)
public class APIQueryInterfaceMsg extends APIQueryMessage {
}
