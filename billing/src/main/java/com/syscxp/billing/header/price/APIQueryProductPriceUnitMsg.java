package com.syscxp.billing.header.price;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.billing.ProductPriceUnitInventory;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_PRICE)
@AutoQuery(replyClass = APIQueryProductPriceUnitReply.class, inventoryClass = ProductPriceUnitInventory.class)
public class APIQueryProductPriceUnitMsg extends APIQueryMessage{

}
