package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.vpn.vpn.APIVpnMessage;
import com.syscxp.header.vpn.vpn.VpnConstant;
import com.syscxp.header.vpn.vpn.VpnVO;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"delete"})
public class APIDeleteL3VpnMsg extends APIVpnMessage {
    @APIParam(resourceType = VpnVO.class, checkAccount = true)
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
                ntfy("Delete VpnVO")
                        .resource(uuid, VpnVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
