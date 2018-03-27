package com.syscxp.trustee.header;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.trustee.trustee.TrusteeConstant;

import java.math.BigDecimal;

@Action(services = {TrusteeConstant.SERVICE_ID}, category = TrusteeConstant.ACTION_CATEGORY, names = {"renewal"})
public class APITrusteeRenewalMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String uuid;

    @APIParam(emptyString = false)
    private ProductChargeModel productChargeModel;

    @APIParam(emptyString = false)
    private int duration;

    @APIParam(emptyString = false)
    private BigDecimal cost;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
