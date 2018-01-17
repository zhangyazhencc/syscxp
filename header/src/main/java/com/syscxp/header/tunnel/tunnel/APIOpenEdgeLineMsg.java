package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/1/12
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIOpenEdgeLineMsg extends APIMessage {

    @APIParam(emptyString = false, resourceType = EdgeLineVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update EdgeLineVO")
                        .resource(uuid, EdgeLineVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
