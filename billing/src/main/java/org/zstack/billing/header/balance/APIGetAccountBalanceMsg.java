package org.zstack.billing.header.balance;

import org.zstack.header.billing.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.identity.UserCredentialCheck;
import org.zstack.header.message.APISyncCallMessage;

@UserCredentialCheck
@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIGetAccountBalanceMsg extends APISyncCallMessage {

}
