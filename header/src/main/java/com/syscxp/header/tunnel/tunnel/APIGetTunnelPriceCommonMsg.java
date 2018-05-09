package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.endpoint.EndpointVO;

/**
 * Create by DCY on 2018/4/13
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
public class APIGetTunnelPriceCommonMsg extends APISyncCallMessage {
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false,maxLength = 32,resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String endpointAUuid;
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String endpointZUuid;
    @APIParam(emptyString = false)
    private String portOfferingUuidA;
    @APIParam(emptyString = false)
    private String portOfferingUuidZ;
    @APIParam(emptyString = false,required = false,resourceType = EndpointVO.class)
    private String innerEndpointUuid;
    @APIParam
    private Integer duration;
    @APIParam(emptyString = false,validValues = {"BY_MONTH", "BY_YEAR", "BY_WEEK","BY_DAY"})
    private ProductChargeModel productChargeModel;

    public String getAccountUuid() {
        if(accountUuid == null){
            return getSession().getAccountUuid();
        }else{
            return accountUuid;
        }
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

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

    public String getInnerEndpointUuid() {
        return innerEndpointUuid;
    }

    public void setInnerEndpointUuid(String innerEndpointUuid) {
        this.innerEndpointUuid = innerEndpointUuid;
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
