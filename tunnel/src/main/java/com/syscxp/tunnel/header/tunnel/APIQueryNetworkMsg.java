package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-09-14
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryNetworkReply.class, inventoryClass = NetworkInventory.class)
public class APIQueryNetworkMsg extends APIQueryMessage {
}
