package org.zstack.tunnel.header.endpoint;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.NodeConstant;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-08-23
 */

@Action(category = NodeConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIUpdateEndpointMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String uuid;

    @APIParam(required = false,maxLength = 255)
    private String name;

    @APIParam(required = false,maxLength = 128)
    private String code;

    @APIParam(required = false)
    private Integer enabled;

    @APIParam(required = false)
    private Integer openToCustomers;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
}
