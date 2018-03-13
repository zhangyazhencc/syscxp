package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.SwitchConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Created by DCY on 2017-09-13
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SwitchConstant.ACTION_CATEGORY, names = {"delete"}, adminOnly = true)
public class APIDeleteSwitchPortMsg extends APIMessage {
    @APIParam(emptyString = false, resourceType = SwitchPortVO.class)
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
                ntfy("Delete SwitchPortVO")
                        .resource(uuid, SwitchPortVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
