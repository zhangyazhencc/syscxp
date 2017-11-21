package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.SwitchConstant;

/**
 * Created by DCY on 2017-09-06
 */
@Action(services = {"tunnel"}, category = SwitchConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)

@AutoQuery(replyClass = APIQuerySwitchModelReply.class, inventoryClass = SwitchModelInventory.class)
public class APIQuerySwitchModelMsg extends APIQueryMessage {
}
