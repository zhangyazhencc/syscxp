package com.syscxp.header.tunnel.endpoint;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.edgeLine.APICreateEdgeLineEvent;
import com.syscxp.header.tunnel.edgeLine.EdgeLineVO;
import com.syscxp.header.tunnel.node.NodeVO;

/**
 * Created by DCY on 2017-08-23
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = NodeConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreateEndpointMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = NodeVO.class)
    private String nodeUuid;

    @APIParam(emptyString = false,maxLength = 255)
    private String name;

    @APIParam(emptyString = false,maxLength = 128)
    private String code;

    @APIParam(emptyString = false,validValues = {"CLOUD","ACCESSIN","INTERCONNECTED","VIRTUAL"})
    private EndpointType endpointType;

    @APIParam(emptyString = false, required = false,resourceType = CloudVO.class)
    private String cloudType;

    @APIParam(emptyString = false,validValues = {"Disabled","Enabled"})
    private EndpointState state;

    @APIParam(emptyString = false,validValues = {"Open","Close"})
    private EndpointStatus status;

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

    public String getCloudType() {
        return cloudType;
    }

    public void setCloudType(String cloudType) {
        this.cloudType = cloudType;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateEndpointEvent) evt).getInventory().getUuid();
                }
                ntfy("Create EndpointVO")
                        .resource(uuid, EndpointVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
