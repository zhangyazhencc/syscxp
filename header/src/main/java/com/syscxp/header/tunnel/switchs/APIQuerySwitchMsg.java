package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.SwitchConstant;

/**
 * Created by DCY on 2017-08-29
 */
@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"read"})

@AutoQuery(replyClass = APIQuerySwitchReply.class, inventoryClass = SwitchInventory.class)
public class APIQuerySwitchMsg extends APIQueryMessage {
}
