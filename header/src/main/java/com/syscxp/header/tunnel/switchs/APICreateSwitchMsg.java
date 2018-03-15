package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.SwitchConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.endpoint.EndpointVO;

/**
 * Created by DCY on 2017-08-29
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SwitchConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreateSwitchMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = EndpointVO.class)
    private String endpointUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String code;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,maxLength = 32,validValues = {"ACCESS", "INNER","OUTER"})
    private SwitchType type;
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

    public SwitchType getType() {
        return type;
    }

    public void setType(SwitchType type) {
        this.type = type;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateSwitchEvent) evt).getInventory().getUuid();
                }
                ntfy("Create SwitchVO")
                        .resource(uuid, SwitchVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
