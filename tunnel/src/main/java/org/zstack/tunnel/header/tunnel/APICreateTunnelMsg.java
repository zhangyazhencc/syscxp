package org.zstack.tunnel.header.tunnel;

import org.zstack.header.identity.AccountType;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.header.switchs.SwitchPortAttribute;
import org.zstack.tunnel.manage.TunnelConstant;

import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
@Action(category = TunnelConstant.ACTION_CATEGORY)
public class APICreateTunnelMsg extends APIMessage {

    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false,resourceType = NetworkVO.class, checkAccount = true)
    private String networkUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam
    private Long bandwidth;
    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceAUuid;
    @APIParam(emptyString = false,validValues = {"Enabled", "Disabled"})
    private TunnelQinqState qinqStateA;
    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceZUuid;
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
        if(getSession().getType() == AccountType.SystemAdmin){
            return accountUuid;
        }else{
            return getSession().getAccountUuid();
        }
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
}
