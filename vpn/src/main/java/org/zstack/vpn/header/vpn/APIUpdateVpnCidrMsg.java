package org.zstack.vpn.header.vpn;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
import org.zstack.vpn.vpn.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"update"}, adminOnly = true)
public class APIUpdateVpnCidrMsg extends APIMessage{
    @APIParam(resourceType = VpnVO.class, checkAccount = true)
    private String uuid;
    @APIParam
    private String vpnCidr;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVpnCidr() {
        return vpnCidr;
    }

    public void setVpnCidr(String vpnCidr) {
        this.vpnCidr = vpnCidr;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update VpnVO vpnCidr")
                        .resource(uuid, VpnVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
