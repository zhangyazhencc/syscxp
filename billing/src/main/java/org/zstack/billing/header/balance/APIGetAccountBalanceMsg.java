package org.zstack.billing.header.balance;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIGetAccountBalanceMsg extends APISyncCallMessage {

}
