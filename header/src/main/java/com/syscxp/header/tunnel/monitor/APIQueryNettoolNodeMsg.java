package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.host.MonitorHostInventory;
import com.syscxp.header.tunnel.node.NodeInventory;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: 速度测试查询.
 */

@SuppressCredentialCheck
@AutoQuery(replyClass = APIQueryNettoolNodeReply.class, inventoryClass = MonitorHostInventory.class)
public class APIQueryNettoolNodeMsg extends APISyncCallMessage {

}
