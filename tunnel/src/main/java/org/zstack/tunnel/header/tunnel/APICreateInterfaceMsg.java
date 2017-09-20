package org.zstack.tunnel.header.tunnel;

import org.zstack.header.identity.AccountType;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.header.endpoint.EndpointType;
import org.zstack.tunnel.header.endpoint.EndpointVO;
import org.zstack.tunnel.header.switchs.SwitchPortAttribute;
import org.zstack.tunnel.header.switchs.SwitchPortType;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-09-08
 */
@Action(category = TunnelConstant.ACTION_CATEGORY)
public class APICreateInterfaceMsg extends APIMessage {

    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,maxLength = 32,resourceType = EndpointVO.class)
    private String endpointUuid;
    @APIParam
    private Long bandwidth;
    @APIParam(emptyString = false,validValues = {"Exclusive", "Shared"})
    private SwitchPortAttribute portAttribute;
    @APIParam(emptyString = false,required = false,validValues = {"RJ45", "SFP_1G","SFG_10G"})
    private SwitchPortType portType;
    @APIParam(emptyString = false,required = false,maxLength = 255)
    private String description;
    @APIParam
    private Integer months;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SwitchPortType getPortType() {
        return portType;
    }

    public void setPortType(SwitchPortType portType) {
        this.portType = portType;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

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

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public SwitchPortAttribute getPortAttribute() {
        return portAttribute;
    }

    public void setPortAttribute(SwitchPortAttribute portAttribute) {
        this.portAttribute = portAttribute;
    }
}
