package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: 速度测试查询.
 */
@AutoQuery(replyClass = APIQuerySpeedTestTunnelReply.class, inventoryClass = SpeedTestTunnelVO.class)
public class APIQuerySpeedTestTunnelMsg extends APIQueryMessage {
}
