package com.syscxp.tunnel.header.host;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by DCY on 2017-08-30
 */
@AutoQuery(replyClass = APIQueryHostReply.class, inventoryClass = HostInventory.class)
public class APIQueryHostMsg extends APIQueryMessage {
}
