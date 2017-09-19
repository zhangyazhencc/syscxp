package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.header.endpoint.EndpointVO;

import java.util.List;

/**
 * Created by DCY on 2017-09-15
 */
public class APICreateTunnelManualMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false,resourceType = NetworkVO.class, checkAccount = true)
    private String networkUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam
    private Long bandwidth;
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
    private Integer months;
    @APIParam(emptyString = false,required = false)
    private String description;
    @APIParam(required = false)
    private List<InnerVlanSegment> vlanSegmentA;
    @APIParam(required = false)
    private List<InnerVlanSegment> vlanSegmentZ;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getNetworkUuid() {
        return networkUuid;
    }

    public void setNetworkUuid(String networkUuid) {
        this.networkUuid = networkUuid;
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

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<InnerVlanSegment> getVlanSegmentA() {
        return vlanSegmentA;
    }

    public void setVlanSegmentA(List<InnerVlanSegment> vlanSegmentA) {
        this.vlanSegmentA = vlanSegmentA;
    }

    public List<InnerVlanSegment> getVlanSegmentZ() {
        return vlanSegmentZ;
    }

    public void setVlanSegmentZ(List<InnerVlanSegment> vlanSegmentZ) {
        this.vlanSegmentZ = vlanSegmentZ;
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
}
