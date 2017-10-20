package com.syscxp.tunnel.header.endpoint;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.manage.NodeConstant;

/**
 * Created by DCY on 2017-08-23
 */

@Action(category = NodeConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIUpdateEndpointMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String uuid;

    @APIParam(required = false,emptyString = false,maxLength = 255)
    private String name;

    @APIParam(required = false,emptyString = false,maxLength = 128)
    private String code;

    @APIParam(required = false,emptyString = false,validValues = {"Enabled","Disabled"})
    private EndpointState state;

    @APIParam(required = false,emptyString = false,validValues = {"Open","Close"})
    private EndpointStatus status;

    @APIParam(required = false,maxLength = 255)
    private String description;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EndpointState getState() {
        return state;
    }

    public void setState(EndpointState state) {
        this.state = state;
    }

    public EndpointStatus getStatus() {
        return status;
    }

    public void setStatus(EndpointStatus status) {
        this.status = status;
    }
}
