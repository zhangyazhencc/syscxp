package com.syscxp.header.vpn.vpn;

import com.syscxp.header.configuration.BandwidthOfferingInventory;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
@AutoQuery(replyClass = APIQueryBandwidthOfferingReply.class, inventoryClass = BandwidthOfferingInventory.class)
public class APIQueryBandwidthOfferingMsg extends APIQueryMessage {
}
