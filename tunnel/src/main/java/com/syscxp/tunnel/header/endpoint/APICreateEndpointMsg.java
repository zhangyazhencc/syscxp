package com.syscxp.tunnel.header.endpoint;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.node.NodeVO;
import com.syscxp.tunnel.manage.NodeConstant;

/**
 * Created by DCY on 2017-08-23
 */

@Action(category = NodeConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreateEndpointMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = NodeVO.class)
    private String nodeUuid;

    @APIParam(emptyString = false,maxLength = 255)
    private String name;

    @APIParam(emptyString = false,maxLength = 128)
    private String code;

    @APIParam(emptyString = false,validValues = {"CLOUD","ACCESSIN","INTERCONNECTED"})
    private EndpointType endpointType;

    @APIParam(required = false,maxLength = 255)
    private String description;

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
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

    public EndpointType getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(EndpointType endpointType) {
        this.endpointType = endpointType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
