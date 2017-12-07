package com.syscxp.header.vpn.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.vpn.VpnConstant;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"update"})
public class APIAttachVpnCertMsg extends APIVpnMessage {
    @APIParam(resourceType = VpnVO.class, checkAccount = true)
    private String uuid;
    @APIParam(resourceType = VpnCertVO.class, checkAccount = true)
    private String vpnCertUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVpnCertUuid() {
        return vpnCertUuid;
    }

    public void setVpnCertUuid(String vpnCertUuid) {
        this.vpnCertUuid = vpnCertUuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Attach VpnVO VpnCert")
                        .resource(uuid, VpnVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
