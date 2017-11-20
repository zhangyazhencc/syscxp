package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.SwitchConstant;

/**
 * Created by DCY on 2017-09-01
 */
@Action(services = {"tunnel"}, category = SwitchConstant.ACTION_CATEGORY, names = {"read"})

@AutoQuery(replyClass = APIQuerySwitchPortReply.class, inventoryClass = SwitchPortInventory.class)
public class APIQuerySwitchPortMsg extends APIQueryMessage {
}
