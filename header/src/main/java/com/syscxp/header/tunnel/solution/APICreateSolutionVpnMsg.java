package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = SolutionConstant.ACTION_CATEGORY, names = "create")
public class APICreateSolutionVpnMsg extends  APIMessage {

    @APIParam(maxLength = 32)
    private String solutionUuid;
    @APIParam(maxLength = 32,required = false)
    private String name;
    @APIParam(maxLength = 32)
    private String cost;
    @APIParam(maxLength = 32)
    private String productChargeModel;
    @APIParam(maxLength = 32)
    private int duration;
    @APIParam(maxLength = 128,required = false)
    private String description;

    @APIParam(maxLength = 128)
    private String zoneName;
    @APIParam(maxLength = 128)
    private String endpointName;
    @APIParam(maxLength = 128)
    private Long bandwidth;

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(String productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
