package com.syscxp.header.billing;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.util.List;

@InnerCredentialCheck
@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_ORDER, names = {"read"})
public class APIGetRenewProductPriceMsg extends APISyncCallMessage {

    @APIParam(emptyString = false)
    private String productUuid;

    @APIParam(numberRange = {1,Integer.MAX_VALUE})
    private int duration;

    @APIParam(emptyString = false)
    private ProductChargeModel productChargeModel;

    @APIParam
    private String accountUuid;

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
