package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APISyncCallMessage;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_DISCOUNT, names = {"read"})
public class APIGetAccountDiscountCategoryMsg extends APISyncCallMessage {

}
