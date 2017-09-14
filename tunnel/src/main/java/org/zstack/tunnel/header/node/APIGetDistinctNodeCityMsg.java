package org.zstack.tunnel.header.node;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APISyncCallMessage;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
import org.zstack.tunnel.manage.NodeConstant;

/**
 * Created by DCY on 2017-08-21
 */
@Action(category = NodeConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)

@AutoQuery(replyClass = APIQueryNodeReply.class, inventoryClass = NodeInventory.class)
public class APIGetDistinctNodeCityMsg extends APISyncCallMessage {
}
