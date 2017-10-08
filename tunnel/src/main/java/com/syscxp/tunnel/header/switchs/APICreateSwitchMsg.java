package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.endpoint.EndpointVO;
import com.syscxp.tunnel.manage.SwitchConstant;

/**
 * Created by DCY on 2017-08-29
 */
@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)

public class APICreateSwitchMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = EndpointVO.class)
    private String endpointUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String code;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,maxLength = 32,resourceType = PhysicalSwitchVO.class)
    private String physicalSwitchUuid;
    @APIParam(emptyString = false,required = false,maxLength = 255)
    private String description;

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhysicalSwitchUuid() {
        return physicalSwitchUuid;
    }

    public void setPhysicalSwitchUuid(String physicalSwitchUuid) {
        this.physicalSwitchUuid = physicalSwitchUuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
