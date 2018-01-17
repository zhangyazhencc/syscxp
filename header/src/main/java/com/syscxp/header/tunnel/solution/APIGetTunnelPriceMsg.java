package com.syscxp.header.tunnel.solution;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.endpoint.InnerConnectedEndpointVO;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "read")
public class APIGetTunnelPriceMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String endpointAUuid;
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String endpointZUuid;
    @APIParam(emptyString = false,required = false,resourceType = SolutionInterfaceVO.class)
    private String interfaceAUuid;
    @APIParam(emptyString = false,required = false,resourceType = SolutionInterfaceVO.class)
    private String interfaceZUuid;
    @APIParam(emptyString = false,required = false,resourceType = InnerConnectedEndpointVO.class)
    private String innerConnectedEndpointUuid;
    @APIParam
    private Integer duration;
    @APIParam(emptyString = false,validValues = {"BY_MONTH", "BY_YEAR","BY_DAY"})
    private ProductChargeModel productChargeModel;

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getEndpointAUuid() {
        return endpointAUuid;
    }

    public void setEndpointAUuid(String endpointAUuid) {
        this.endpointAUuid = endpointAUuid;
    }

    public String getEndpointZUuid() {
        return endpointZUuid;
    }

    public void setEndpointZUuid(String endpointZUuid) {
        this.endpointZUuid = endpointZUuid;
    }

    public String getInterfaceAUuid() {
        return interfaceAUuid;
    }

    public void setInterfaceAUuid(String interfaceAUuid) {
        this.interfaceAUuid = interfaceAUuid;
    }

    public String getInterfaceZUuid() {
        return interfaceZUuid;
    }

    public void setInterfaceZUuid(String interfaceZUuid) {
        this.interfaceZUuid = interfaceZUuid;
    }

    public String getInnerConnectedEndpointUuid() {
        return innerConnectedEndpointUuid;
    }

    public void setInnerConnectedEndpointUuid(String innerConnectedEndpointUuid) {
        this.innerConnectedEndpointUuid = innerConnectedEndpointUuid;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }
}
