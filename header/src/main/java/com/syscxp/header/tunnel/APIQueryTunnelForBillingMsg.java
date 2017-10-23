package com.syscxp.header.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by DCY on 2017-09-17
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryTunnelForBillingReply.class, inventoryClass = TunnelForBillingInventory.class)
@InnerCredentialCheck
public class APIQueryTunnelForBillingMsg extends APIQueryMessage {
}
