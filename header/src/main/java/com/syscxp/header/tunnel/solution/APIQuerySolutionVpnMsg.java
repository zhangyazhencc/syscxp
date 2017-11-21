package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by wangwg on 2017/11/21
 */

@Action(category = SolutionConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQuerySolutionVpnReply.class, inventoryClass = SolutionVpnInventory.class)
public class APIQuerySolutionVpnMsg extends APIQueryMessage {
}
