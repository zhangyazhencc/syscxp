package org.zstack.vpn.header.host;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
import org.zstack.vpn.manage.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"update"}, adminOnly = true)
public class APIUpdateVpnHostStateMsg extends APIMessage{
    @APIParam
    private String uuid;
    @APIParam(required = false)
    private HostState state;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public HostState getState() {
        return state;
    }

    public void setState(HostState state) {
        this.state = state;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update VpnHostVO state")
                        .resource(uuid, VpnHostVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
