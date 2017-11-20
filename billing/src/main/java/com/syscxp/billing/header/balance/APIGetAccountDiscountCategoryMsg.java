package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APISyncCallMessage;

@Action(category = BillingConstant.ACTION_CATEGORY_BILLING, names = {"read"})
public class APIGetAccountDiscountCategoryMsg extends APISyncCallMessage {

}
