package com.syscxp.idc.header;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.idc.IdcConstant;
import java.math.BigDecimal;

@Action(services = {IdcConstant.SERVICE_ID}, category = IdcConstant.ACTION_CATEGORY, names = {"renewal"})
public class APIIdcRenewalMsg extends APIMessage {

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
