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

    @APIParam(emptyString = false,maxLength = 255)
    private String name;

    @APIParam(emptyString = false,maxLength = 128)
    private String code;

    @APIParam(emptyString = false,maxLength = 1)
    private Integer enabled;

    @APIParam(emptyString = false,maxLength = 1)
    public Integer openToCustomers;

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

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getOpenToCustomers() {
        return openToCustomers;
    }

    public void setOpenToCustomers(Integer openToCustomers) {
        this.openToCustomers = openToCustomers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
