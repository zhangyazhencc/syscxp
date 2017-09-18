package org.zstack.tunnel.header.tunnel;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.header.endpoint.EndpointType;
import org.zstack.tunnel.header.switchs.SwitchPortType;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-09-08
 */
@Action(category = TunnelConstant.ACTION_CATEGORY)
public class APICreateInterfaceMsg extends APIMessage {

    @APIParam(required = false,maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,maxLength = 32)
    private String endpointUuid;
    @APIParam(emptyString = false)
    private Integer bandwidth;
    @APIParam(emptyString = false)
    private Integer isExclusive;
    @APIParam(emptyString = false,validValues = {"CLOUD", "ACCESSIN"})
    private EndpointType endpointType;
    @APIParam(required = false,validValues = {"RJ45", "SFP_1G","SFG_10G"})
    private SwitchPortType portType;
    @APIParam(required = false,maxLength = 255)
    private String description;
    @APIParam(emptyString = false)
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

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Integer getIsExclusive() {
        return isExclusive;
    }

    public void setIsExclusive(Integer isExclusive) {
        this.isExclusive = isExclusive;
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

    public EndpointType getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(EndpointType endpointType) {
        this.endpointType = endpointType;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public String getAccountUuid() {


        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
