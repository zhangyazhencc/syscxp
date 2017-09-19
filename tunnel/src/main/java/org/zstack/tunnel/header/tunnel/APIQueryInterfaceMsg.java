package org.zstack.tunnel.header.tunnel;

import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-09-11
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryInterfaceReply.class, inventoryClass = InterfaceInventory.class)
public class APIQueryInterfaceMsg extends APIQueryMessage {
}
