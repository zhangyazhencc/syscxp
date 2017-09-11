package org.zstack.tunnel.header.switchs;

import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
import org.zstack.tunnel.manage.SwitchConstant;

/**
 * Created by DCY on 2017-08-29
 */
@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)

@AutoQuery(replyClass = APIQuerySwitchReply.class, inventoryClass = SwitchToEndpointAndModelInventory.class)
public class APIQuerySwitchMsg extends APIQueryMessage {
}
