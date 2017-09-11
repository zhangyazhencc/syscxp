package org.zstack.billing.header.receipt;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APISyncCallMessage;

@Action(category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"read"})
public class APIGetValuebleReceiptMsg extends APISyncCallMessage {


}
