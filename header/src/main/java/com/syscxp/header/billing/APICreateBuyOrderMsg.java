package com.syscxp.header.billing;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;

import java.util.List;

@InnerCredentialCheck
@Action(category = BillingConstant.ACTION_CATEGORY_ORDER)
public class APICreateBuyOrderMsg extends APICreateOrderMsg {

    @APIParam(emptyString = false)
    private ProductChargeModel productChargeModel;

    @APIParam(numberRange = {1,Integer.MAX_VALUE})
    private int duration;


    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public APICreateBuyOrderMsg(APICreateOrderMsg msg) {
        super(msg);
    }

    public APICreateBuyOrderMsg() {
    }
}
