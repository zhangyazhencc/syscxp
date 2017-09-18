package org.zstack.vpn.header.gateway;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
import org.zstack.vpn.manage.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"update"}, adminOnly = true)
public class APIUpdateTunnelIfaceMsg extends APIMessage{
    @APIParam(resourceType = TunnelIfaceVO.class)
    private String uuid;
    @APIParam
    private String name;

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


    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update TunnelIfaceVO")
                        .resource(uuid, TunnelIfaceVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
