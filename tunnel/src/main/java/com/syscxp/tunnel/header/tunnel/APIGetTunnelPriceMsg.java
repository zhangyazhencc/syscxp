package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.tunnel.header.endpoint.EndpointVO;
import com.syscxp.tunnel.header.node.NodeVO;

/**
 * Create by DCY on 2017/11/1
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
public class APIGetTunnelPriceMsg extends APISyncCallMessage {
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false,maxLength = 32,resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;
    @APIParam(emptyString = false,resourceType = NodeVO.class)
    private String nodeAUuid;
    @APIParam(emptyString = false,resourceType = NodeVO.class)
    private String nodeZUuid;
    @APIParam(emptyString = false,required = false,resourceType = EndpointVO.class)
    private String innerEndpointUuid;
    @APIParam
    private Integer duration;
    @APIParam(emptyString = false,validValues = {"BY_MONTH", "BY_YEAR","BY_DAY"})
    private ProductChargeModel productChargeModel;

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

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getNodeAUuid() {
        return nodeAUuid;
    }

    public void setNodeAUuid(String nodeAUuid) {
        this.nodeAUuid = nodeAUuid;
    }

    public String getNodeZUuid() {
        return nodeZUuid;
    }

    public void setNodeZUuid(String nodeZUuid) {
        this.nodeZUuid = nodeZUuid;
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
