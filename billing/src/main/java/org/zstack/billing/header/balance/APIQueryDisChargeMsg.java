package org.zstack.billing.header.balance;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"recharge", "read"})
public class APIQueryDisChargeMsg  extends APIQueryMessage {
}
