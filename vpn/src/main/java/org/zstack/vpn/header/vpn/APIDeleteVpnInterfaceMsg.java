package org.zstack.vpn.header.vpn;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIDeleteMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
import org.zstack.vpn.vpn.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"delete"}, adminOnly = true)
public class APIDeleteVpnInterfaceMsg extends APIDeleteMessage {
    @APIParam(resourceType = VpnInterfaceVO.class)
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
                ntfy("Delete VpnInterfaceVO")
                        .resource(uuid, VpnInterfaceVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
