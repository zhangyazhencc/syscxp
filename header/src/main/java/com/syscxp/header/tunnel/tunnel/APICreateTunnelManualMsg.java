package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.endpoint.InnerConnectedEndpointVO;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.TunnelConstant;

import java.util.List;

/**
 * Created by DCY on 2017-09-15
 */
@Action(services = {"tunnel"}, category = TunnelConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreateTunnelManualMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,maxLength = 32,resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;
    @APIParam(emptyString = false,resourceType = NodeVO.class)
    private String nodeAUuid;
    @APIParam(emptyString = false,resourceType = NodeVO.class)
    private String nodeZUuid;
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String endpointAUuid;
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String endpointZUuid;
    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceAUuid;
    @APIParam(numberRange = {1, 4094})
    private Integer aVlan;
    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceZUuid;
    @APIParam(numberRange = {1, 4094})
    private Integer zVlan;
    @APIParam
    private Integer duration;
    @APIParam(emptyString = false,validValues = {"BY_MONTH", "BY_YEAR","BY_DAY"})
    private ProductChargeModel productChargeModel;
    @APIParam(emptyString = false,required = false)
    private String description;
    @APIParam(required = false)
    private List<InnerVlanSegment> vlanSegment;
    @APIParam(emptyString = false,required = false,maxLength = 32,resourceType = EndpointVO.class)
    private String innerConnectedEndpointUuid;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterfaceAUuid() {
        return interfaceAUuid;
    }

    public void setInterfaceAUuid(String interfaceAUuid) {
        this.interfaceAUuid = interfaceAUuid;
    }

    public Integer getaVlan() {
        return aVlan;
    }

    public void setaVlan(Integer aVlan) {
        this.aVlan = aVlan;
    }

    public String getInterfaceZUuid() {
        return interfaceZUuid;
    }

    public void setInterfaceZUuid(String interfaceZUuid) {
        this.interfaceZUuid = interfaceZUuid;
    }

    public Integer getzVlan() {
        return zVlan;
    }

    public void setzVlan(Integer zVlan) {
        this.zVlan = zVlan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<InnerVlanSegment> getVlanSegment() {
        return vlanSegment;
    }

    public void setVlanSegment(List<InnerVlanSegment> vlanSegment) {
        this.vlanSegment = vlanSegment;
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


    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getInnerConnectedEndpointUuid() {
        return innerConnectedEndpointUuid;
    }

    public void setInnerConnectedEndpointUuid(String innerConnectedEndpointUuid) {
        this.innerConnectedEndpointUuid = innerConnectedEndpointUuid;
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
}
