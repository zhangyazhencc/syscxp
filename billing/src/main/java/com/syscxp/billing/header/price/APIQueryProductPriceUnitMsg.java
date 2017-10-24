package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnitInventory;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryProductPriceUnitReply.class, inventoryClass = ProductPriceUnitInventory.class)
public class APIQueryProductPriceUnitMsg extends APIQueryMessage{

}
