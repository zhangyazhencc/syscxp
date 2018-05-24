package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.CloudHubConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/5/23
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = CloudHubConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryCloudHubOfferingReply.class, inventoryClass = CloudHubOfferingInventory.class)
public class APIQueryCloudHubOfferingMsg extends APIQueryMessage {

}
