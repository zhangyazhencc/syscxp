package com.syscxp.header.idc.solution;

import com.syscxp.header.idc.IdcConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.idc.SolutionConstant;

/**
 * Created by wangwg on 2017/11/21
 */

@Action(services = {IdcConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQuerySolutionReply.class, inventoryClass = SolutionInventory.class)
public class APIQuerySolutionMsg extends APIQueryMessage {
}
