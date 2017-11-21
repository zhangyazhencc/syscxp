package com.syscxp.header.tunnel.host;

import com.syscxp.header.host.APIQueryHostMsg;
import com.syscxp.header.host.HostConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.AutoQuery;

@Action(services = {"tunnel"}, category = HostConstant.ACTION_CATEGORY, adminOnly = true)
@AutoQuery(replyClass = APIQueryMonitorHostReply.class, inventoryClass = MonitorHostInventory.class)
public class APIQueryMonitorHostMsg extends APIQueryHostMsg {

}
