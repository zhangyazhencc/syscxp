package com.syscxp.alarm.header.contact;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@Action(category = AlarmConstant.ACTION_CATEGORY_CONTACT)
@AutoQuery(replyClass = APIQueryContactGroupReply.class, inventoryClass = ContactGroupInventory.class)
public class APIQueryContactGroupMsg extends APIQueryMessage {
}
