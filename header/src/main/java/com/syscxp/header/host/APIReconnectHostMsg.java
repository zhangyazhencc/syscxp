package com.syscxp.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(category = HostConstant.ACTION_CATEGORY, adminOnly = true)
public class APIReconnectHostMsg extends APIMessage implements HostMessage {
    /**
     * @desc host uuid
     */
    @APIParam(resourceType = HostVO.class)
    private String uuid;

    public APIReconnectHostMsg() {
    }

    public APIReconnectHostMsg(String uuid) {
        super();
        this.uuid = uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String getHostUuid() {
        return getUuid();
    }

 
    public static APIReconnectHostMsg __example__() {
        APIReconnectHostMsg msg = new APIReconnectHostMsg();
        msg.setUuid(uuid());
        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Reconnected").resource(uuid, HostVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
