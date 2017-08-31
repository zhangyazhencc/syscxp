package org.zstack.billing.header.identity.balance;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APISyncCallMessage;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"read", "balance"})
public class APIGetAccountBalanceMsg extends APISyncCallMessage {

}
