package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@AutoQuery(replyClass = APIQueryHostSwitchMonitorReply.class, inventoryClass = HostSwitchMonitorInventory.class)
public class APIQueryHostSwitchMonitorMsg extends APIQueryMessage {
}
