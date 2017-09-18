package org.zstack.vpn.header.gateway;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
import org.zstack.vpn.manage.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"update"}, adminOnly = true)
public class APIUpdateVpnBindwidthMsg extends APIMessage{
    @APIParam(resourceType = VpnGatewayVO.class, checkAccount = true)
    private String uuid;
    @APIParam
    private Integer bandwidth;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update VpnGatewayVO bandwidth")
                        .resource(uuid, VpnGatewayVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
