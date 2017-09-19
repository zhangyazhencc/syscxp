package org.zstack.tunnel.header.tunnel;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by DCY on 2017-09-14
 */
@AutoQuery(replyClass = APIQueryNetworkReply.class, inventoryClass = NetworkInventory.class)
public class APIQueryNetworkMsg extends APIQueryMessage {
}
