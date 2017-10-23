package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.billing.ProductPriceUnit;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.node.NodeVO;
import com.syscxp.tunnel.header.endpoint.EndpointVO;
import com.syscxp.header.tunnel.TunnelConstant;

import java.util.List;

/**
 * Created by DCY on 2017-09-15
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreateTunnelManualMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String accountUuid;
    @APIParam
    private Integer vsi;
    @APIParam(emptyString = false,maxLength = 32)
    private String monitorCidr;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam
    private Long bandwidth;
    @APIParam(emptyString = false,resourceType = NodeVO.class)
    private String nodeAUuid;
    @APIParam(emptyString = false,resourceType = NodeVO.class)
    private String nodeZUuid;
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String endpointPointAUuid;
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String endpointPointZUuid;
    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceAUuid;
    @APIParam(numberRange = {1, 4094})
    private Integer aVlan;
    @APIParam(emptyString = false,validValues = {"Enabled", "Disabled"})
    private TunnelQinqState qinqStateA;
    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceZUuid;
    @APIParam(numberRange = {1, 4094})
    private Integer zVlan;
    @APIParam(emptyString = false,validValues = {"Enabled", "Disabled"})
    private TunnelQinqState qinqStateZ;
    @APIParam
    private Integer duration;
    @APIParam(emptyString = false,validValues = {"BY_MONTH", "BY_YEAR","BY_DAY"})
    private ProductChargeModel productChargeModel;
    @APIParam(nonempty = true)
    private List<ProductPriceUnit> units;
    @APIParam(emptyString = false,required = false)
    private String description;
    @APIParam(required = false)
    private List<InnerVlanSegment> vlanSegment;

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

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
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

    public TunnelQinqState getQinqStateA() {
        return qinqStateA;
    }

    public void setQinqStateA(TunnelQinqState qinqStateA) {
        this.qinqStateA = qinqStateA;
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

    public TunnelQinqState getQinqStateZ() {
        return qinqStateZ;
    }

    public void setQinqStateZ(TunnelQinqState qinqStateZ) {
        this.qinqStateZ = qinqStateZ;
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

    public String getEndpointPointAUuid() {
        return endpointPointAUuid;
    }

    public void setEndpointPointAUuid(String endpointPointAUuid) {
        this.endpointPointAUuid = endpointPointAUuid;
    }

    public String getEndpointPointZUuid() {
        return endpointPointZUuid;
    }

    public void setEndpointPointZUuid(String endpointPointZUuid) {
        this.endpointPointZUuid = endpointPointZUuid;
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

    public List<ProductPriceUnit> getUnits() {
        return units;
    }

    public void setUnits(List<ProductPriceUnit> units) {
        this.units = units;
    }

    public Integer getVsi() {
        return vsi;
    }

    public void setVsi(Integer vsi) {
        this.vsi = vsi;
    }

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }
}
