package com.syscxp.idc.header;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.idc.idc.IdcConstant;

@AutoQuery(replyClass = APIQueryIdcDetailReply.class, inventoryClass = IdcDetailInventory.class)
@Action(services = {IdcConstant.SERVICE_ID}, category = IdcConstant.ACTION_CATEGORY, names = {"read"})
public class APIQueryIdcDetailMsg extends APIQueryMessage {

}
