package com.syscxp.header.tunnel.network;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.TunnelConstant;

import java.sql.Timestamp;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = L3NetWorkConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateL3NetworkMsg extends APIMessage {

    @APIParam(emptyString = false, resourceType = L3NetworkVO.class, checkAccount = true)
    private String uuid;
    @APIParam(emptyString = false, required = false, maxLength = 128)
    private String name;
    @APIParam(required = false)
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update L3NetworkVO")
                        .resource(uuid, L3NetworkVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
