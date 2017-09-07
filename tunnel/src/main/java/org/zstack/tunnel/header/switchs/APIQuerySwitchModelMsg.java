package org.zstack.tunnel.header.switchs;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by DCY on 2017-09-06
 */
@AutoQuery(replyClass = APIQuerySwitchModelReply.class, inventoryClass = SwitchModelInventory.class)
public class APIQuerySwitchModelMsg extends APIQueryMessage {
}
