package com.syscxp.header.tunnel.sla;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.SlaConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-22.
 * @Description: 查询SLA分析数据.
 */

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SlaConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
@AutoQuery(replyClass = APIQuerySlaAnalyzeReply.class, inventoryClass = SlaAnalyzeInventory.class)
public class APIQuerySlaAnalyzeMsg extends APIQueryMessage {
}
