package com.syscxp.header.tunnel.solution;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "update")
public class APIUpdateSolutionTunnelMsg extends  APIMessage {

    @APIParam(maxLength = 32)
    private String uuid;

    @APIParam(maxLength = 128)
    private long bandwidth;

    @APIParam(maxLength = 32)
    private String cost;
    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_DAY"})
    private ProductChargeModel productChargeModel;
    @APIParam(maxLength = 32)
    private int duration;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
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
}
