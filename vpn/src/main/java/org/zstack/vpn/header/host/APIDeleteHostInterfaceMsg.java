package org.zstack.vpn.header.host;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIDeleteMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
import org.zstack.vpn.manage.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"delete"}, adminOnly = true)
public class APIDeleteHostInterfaceMsg extends APIDeleteMessage {
    @APIParam(resourceType = VpnHostVO.class)
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
                ntfy("Delete HostInterfaceVO")
                        .resource(uuid, HostInterfaceVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
