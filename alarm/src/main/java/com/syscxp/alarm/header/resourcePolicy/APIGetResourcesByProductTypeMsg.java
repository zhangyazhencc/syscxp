package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@Action(adminOnly = true,category = AlarmConstant.ACTION_CATEGORY_RESOURCE_POLICY)
public class APIGetResourcesByProductTypeMsg extends APISyncCallMessage{

    @APIParam(emptyString = false)
    private ProductType productType;

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
}
