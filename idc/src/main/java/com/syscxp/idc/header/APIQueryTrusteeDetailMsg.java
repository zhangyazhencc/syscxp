package com.syscxp.idc.header;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.idc.trustee.TrusteeConstant;

@AutoQuery(replyClass = APIQueryTrusteeDetailReply.class, inventoryClass = TrusteeDetailInventory.class)
@Action(services = {TrusteeConstant.SERVICE_ID}, category = TrusteeConstant.ACTION_CATEGORY, names = {"read"})
public class APIQueryTrusteeDetailMsg extends APIQueryMessage {

}
