package org.zstack.header.billing;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.util.List;

@Action(category = "order", names = {"create"})
public class APICreateOrderMsg extends APIMessage {

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

}
