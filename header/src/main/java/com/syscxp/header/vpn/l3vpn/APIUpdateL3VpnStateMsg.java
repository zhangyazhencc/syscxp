package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.vpn.vpn.VpnConstant;
import com.syscxp.header.vpn.vpn.VpnState;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"update"})
public class APIUpdateL3VpnStateMsg extends APIMessage{
    @APIParam(resourceType = L3VpnVO.class, checkAccount = true)
    private String uuid;
    @APIParam
    private VpnState state;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public VpnState getState() {
        return state;
    }

    public void setState(VpnState state) {
        this.state = state;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update L3VpnVO state")
                        .resource(uuid, L3VpnVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
