package com.syscxp.vpn.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIDeleteMessage;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.vpn.vpn.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"delete"}, adminOnly = true)
public class APIDeleteZoneMsg extends APIDeleteMessage {
    @APIParam(resourceType = ZoneVO.class)
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
                ntfy("Delete ZoneVO")
                        .resource(uuid, ZoneVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
