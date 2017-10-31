package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/10/30
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryPortOfferingReply.class, inventoryClass = PortOfferingVO.class)
public class APIQueryPortOfferingMsg extends APIQueryMessage {
}
