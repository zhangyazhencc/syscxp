package org.zstack.tunnel.header.host;

import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-08-30
 */

@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"host"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryHostMonitorReply.class, inventoryClass = HostSwitchPortInventory.class)
public class APIQueryHostMonitorMsg extends APIQueryMessage {
}
