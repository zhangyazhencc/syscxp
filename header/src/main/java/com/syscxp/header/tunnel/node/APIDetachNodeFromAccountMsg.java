package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/5/3
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = NodeConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APIDetachNodeFromAccountMsg extends APIMessage {

    @APIParam(emptyString = false, maxLength = 32, resourceType = AccountNodeRefVO.class)
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
                ntfy("Detach Node From Account")
                        .resource(uuid, AccountNodeRefVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
