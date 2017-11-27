package com.syscxp.header.tunnel.solution;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.tunnel.BandwidthOfferingVO;

import java.math.BigDecimal;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "create")
public class APICreateSolutionTunnelMsg extends  APIMessage {

    @APIParam(maxLength = 32)
    private String solutionUuid;
    @APIParam(numberRange = {0,Long.MAX_VALUE})
    private BigDecimal cost;
    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_DAY"})
    private ProductChargeModel productChargeModel;
    @APIParam(maxLength = 32)
    private int duration;

    @APIParam(maxLength = 128)
    private String endpointUuidA;
    @APIParam(maxLength = 128)
    private String endpointUuidZ;

    @APIParam(emptyString = false,maxLength = 32,resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;
    @APIParam(maxLength = 128)
    private long bandwidth;

    @APIParam(emptyString = false,required = false,resourceType = EndpointVO.class)
    private String innerEndpointUuid;

    @APIParam(maxLength = 128, required = false)
    private String portOfferingUuidA;
    @APIParam(maxLength = 128, required = false)
    private String portOfferingUuidZ;
    @APIParam(required = false,maxLength = 32)
    private String accountUuid;

    public String getAccountUuid() {
        if(getSession().getType() == AccountType.SystemAdmin){
            return accountUuid;
        }else{
            return getSession().getAccountUuid();
        }
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }


    public String getSolutionUuid() {
        return solutionUuid;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
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

    public String getEndpointUuidA() {
        return endpointUuidA;
    }

    public void setEndpointUuidA(String endpointUuidA) {
        this.endpointUuidA = endpointUuidA;
    }

    public String getEndpointUuidZ() {
        return endpointUuidZ;
    }

    public void setEndpointUuidZ(String endpointUuidZ) {
        this.endpointUuidZ = endpointUuidZ;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getPortOfferingUuidA() {
        return portOfferingUuidA;
    }

    public void setPortOfferingUuidA(String portOfferingUuidA) {
        this.portOfferingUuidA = portOfferingUuidA;
    }

    public String getPortOfferingUuidZ() {
        return portOfferingUuidZ;
    }

    public void setPortOfferingUuidZ(String portOfferingUuidZ) {
        this.portOfferingUuidZ = portOfferingUuidZ;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getInnerEndpointUuid() {
        return innerEndpointUuid;
    }

    public void setInnerEndpointUuid(String innerEndpointUuid) {
        this.innerEndpointUuid = innerEndpointUuid;
    }
}
