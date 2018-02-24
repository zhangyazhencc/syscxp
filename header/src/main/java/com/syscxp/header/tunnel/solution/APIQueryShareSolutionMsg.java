package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryShareSolutionReply.class, inventoryClass = ShareSolutionInventory.class)
public class APIQueryShareSolutionMsg {
}
