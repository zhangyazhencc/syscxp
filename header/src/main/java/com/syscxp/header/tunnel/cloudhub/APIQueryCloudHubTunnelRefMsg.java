package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.CloudHubConstant;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = CloudHubConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryCloudHubTunnelRefReply.class, inventoryClass = CloudHubTunnelRefInventory.class)
public class APIQueryCloudHubTunnelRefMsg extends APIQueryMessage {

}
