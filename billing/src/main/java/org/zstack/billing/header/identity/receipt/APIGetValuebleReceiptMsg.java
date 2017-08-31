package org.zstack.billing.header.identity.receipt;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APISyncCallMessage;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"read", "receipt"})
public class APIGetValuebleReceiptMsg extends APISyncCallMessage {


}
