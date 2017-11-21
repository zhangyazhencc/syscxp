package com.syscxp.header.tunnel.host;

import com.syscxp.header.host.HostConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = HostConstant.ACTION_CATEGORY, adminOnly = true, names = {"read"})
@AutoQuery(replyClass = APIQueryHostSwitchMonitorReply.class, inventoryClass = HostSwitchMonitorInventory.class)
public class APIQueryHostSwitchMonitorMsg extends APIQueryMessage {
}
