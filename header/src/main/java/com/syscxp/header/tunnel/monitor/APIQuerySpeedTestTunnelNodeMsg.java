package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: 速度测试查询.
 */

@SuppressCredentialCheck
@AutoQuery(replyClass = APIQuerySpeedTestTunnelNodeReply.class, inventoryClass = SpeedTestTunnelNodeInventory.class)
public class APIQuerySpeedTestTunnelNodeMsg extends APIQueryMessage {
}
