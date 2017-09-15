package org.zstack.tunnel.header.monitor;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
import org.zstack.tunnel.header.host.HostInventory;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@AutoQuery(replyClass = APIQueryHostSwitchMonitorReply.class, inventoryClass = HostSwitchMonitorInventory.class)
public class APIQueryHostSwitchMonitorMsg extends APIQueryMessage {
}
