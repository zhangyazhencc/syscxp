package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = SolutionConstant.ACTION_CATEGORY, names = "create")
public class APICreateSolutionTunnelMsg extends  APIMessage {

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
    private String endpointNameA;
    @APIParam(maxLength = 128)
    private String endpointNameZ;
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

    public String getEndpointNameA() {
        return endpointNameA;
    }

    public void setEndpointNameA(String endpointNameA) {
        this.endpointNameA = endpointNameA;
    }

    public String getEndpointNameZ() {
        return endpointNameZ;
    }

    public void setEndpointNameZ(String endpointNameZ) {
        this.endpointNameZ = endpointNameZ;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
