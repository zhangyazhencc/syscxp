package com.syscxp.header.billing;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.util.List;

@InnerCredentialCheck
@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_ORDER)
public class APICreateBuyOrderMsg extends APISyncCallMessage{

    @APIParam(nonempty = true)
    private List<ProductInfoForOrder> products;

    public List<ProductInfoForOrder> getProducts() {
        return products;
    }

    public void setProducts(List<ProductInfoForOrder> products) {
        this.products = products;
    }
}
