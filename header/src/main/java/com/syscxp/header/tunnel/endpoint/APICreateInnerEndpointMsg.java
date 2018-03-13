package com.syscxp.header.tunnel.endpoint;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/11/1
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = NodeConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreateInnerEndpointMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = EndpointVO.class)
    private String endpointUuid;

    @APIParam(emptyString = false,maxLength = 32,resourceType = EndpointVO.class)
    private String connectedEndpointUuid;

    @APIParam(emptyString = false,maxLength = 128)
    private String name;

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getConnectedEndpointUuid() {
        return connectedEndpointUuid;
    }

    public void setConnectedEndpointUuid(String connectedEndpointUuid) {
        this.connectedEndpointUuid = connectedEndpointUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateInnerEndpointEvent) evt).getInventory().getUuid();
                }
                ntfy("Create InnerConnectedEndpointVO")
                        .resource(uuid, InnerConnectedEndpointVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
