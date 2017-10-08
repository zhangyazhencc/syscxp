package com.syscxp.billing.header.receipt;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APISyncCallMessage;

@Action(category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"read"})
public class APIGetValuebleReceiptMsg extends APISyncCallMessage {


}
