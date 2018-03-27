package com.syscxp.trustee.header;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.trustee.trustee.TrusteeConstant;


@AutoQuery(replyClass = APIQueryTrusteeReply.class, inventoryClass = TrusteeInventory.class)
@Action(services = {TrusteeConstant.SERVICE_ID}, category = TrusteeConstant.ACTION_CATEGORY, names = {"read"})
public class APIQueryTrusteeMsg extends APIQueryMessage {

}
