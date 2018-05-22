package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.CloudHubConstant;

@Action(services = {CloudHubConstant.ACTION_SERVICE}, category = CloudHubConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryCloudHubReply.class, inventoryClass = CloudHubInventory.class)
public class APIQueryCloudHubMsg extends APIQueryMessage {

}
