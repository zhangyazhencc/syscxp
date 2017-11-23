package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.node.APIQueryNodeReply;
import com.syscxp.header.tunnel.node.NodeInventory;

/**
 * Created by wangwg on 2017/11/21
 */

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQuerySolutionReply.class, inventoryClass = SolutionInventory.class)
public class APIQuerySolutionMsg extends APIQueryMessage {
}
