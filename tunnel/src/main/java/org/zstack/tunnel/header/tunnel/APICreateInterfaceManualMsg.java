package org.zstack.tunnel.header.tunnel;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.header.endpoint.EndpointVO;
import org.zstack.tunnel.header.switchs.SwitchPortAttribute;
import org.zstack.tunnel.header.switchs.SwitchPortVO;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-09-11
 */
@Action(category = TunnelConstant.ACTION_CATEGORY)
public class APICreateInterfaceManualMsg extends APIMessage {


    @APIParam(emptyString = false,maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,maxLength = 32,resourceType = SwitchPortVO.class)
    private String switchPortUuid;
    @APIParam(emptyString = false,maxLength = 32,resourceType = EndpointVO.class)
    private String endpointUuid;
    @APIParam
    private Long bandwidth;
    @APIParam(emptyString = false,required = false,maxLength = 255)
    private String description;
    @APIParam(emptyString = false)
    private Integer months;

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

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
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

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
