package org.zstack.header.billing;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;


public class APICreateOrderMsg extends APIMessage {

    @APIParam(emptyString = false)
    private ProductChargeModel productChargeModel;


    @APIParam(numberRange = {1,Integer.MAX_VALUE})
    private int duration;

    @APIParam(emptyString = false)
    private String accountUuid;


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

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
