package org.zstack.header.billing;

import org.zstack.header.identity.Action;
import org.zstack.header.identity.InnerCredentialCheck;
import org.zstack.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY_ORDER)
@InnerCredentialCheck
public class APICreateRenewOrderMsg  extends APICreateOrderMsg {

    @APIParam(emptyString = false)
    private String productUuid;

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

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }
}
