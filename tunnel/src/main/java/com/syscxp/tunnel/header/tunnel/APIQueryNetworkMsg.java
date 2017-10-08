package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by DCY on 2017-09-14
 */
@AutoQuery(replyClass = APIQueryNetworkReply.class, inventoryClass = NetworkInventory.class)
public class APIQueryNetworkMsg extends APIQueryMessage {
}
