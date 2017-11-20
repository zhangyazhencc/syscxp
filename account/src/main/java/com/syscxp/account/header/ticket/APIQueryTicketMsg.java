package com.syscxp.account.header.ticket;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.identity.SuppressUserCredentialCheck;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by wangwg on 2017/09/26.
 */
@AutoQuery(replyClass = APIQueryTicketReply.class, inventoryClass = TicketInventory.class)
@SuppressUserCredentialCheck
@Action(services = {"account"}, category = AccountConstant.ACTION_CATEGORY_TICKET, names = {"read"})
public class APIQueryTicketMsg extends APIQueryMessage {

}
