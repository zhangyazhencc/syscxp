package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.MonitorConstant;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: 速度测试查询.
 */

@Action(services = {"tunnel"}, category = MonitorConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQuerySpeedTestTunnelNodeReply.class, inventoryClass = SpeedTestTunnelNodeInventory.class)
public class APIQuerySpeedTestTunnelNodeMsg extends APIQueryMessage {
}
