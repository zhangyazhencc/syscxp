package com.syscxp.vpn.header.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.vpn.vpn.VpnConstant;

@Action(services = {"vpn"}, category = VpnConstant.ACTION_CATEGORY_VPN)
public class APIResetCertificateMsg extends APIVpnMessage {
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
                ntfy("Reset cert")
                        .resource(uuid, VpnVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
