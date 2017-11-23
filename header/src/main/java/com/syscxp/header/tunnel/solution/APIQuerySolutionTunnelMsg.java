package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Created by wangwg on 2017/11/21
 */

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQuerySolutionTunnelReply.class, inventoryClass = SolutionTunnelInventory.class)
public class APIQuerySolutionTunnelMsg extends APIQueryMessage {
}
