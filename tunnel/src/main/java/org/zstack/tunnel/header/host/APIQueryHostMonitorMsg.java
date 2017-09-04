package org.zstack.tunnel.header.host;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by DCY on 2017-08-30
 */
@AutoQuery(replyClass = APIQueryHostMonitorReply.class, inventoryClass = HostSwitchPortInventory.class)
public class APIQueryHostMonitorMsg extends APIQueryMessage {
}
