package org.zstack.vpn.header.gateway;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
import org.zstack.vpn.manage.EntityState;
import org.zstack.vpn.manage.RunningStatus;
import org.zstack.vpn.manage.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"update"}, adminOnly = true)
public class APIUpdateVpnGatewayMsg extends APIMessage{
    @APIParam(resourceType = VpnGatewayVO.class, checkAccount = true)
    private String uuid;
    @APIParam(required = false)
    private String name;
    @APIParam(required = false)
    private String description;
    @APIParam(required = false)
    private String vpnCidr;
    @APIParam(required = false)
    private EntityState state;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVpnCidr() {
        return vpnCidr;
    }

    public void setVpnCidr(String vpnCidr) {
        this.vpnCidr = vpnCidr;
    }

    public EntityState getState() {
        return state;
    }

    public void setState(EntityState state) {
        this.state = state;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update VpnGatewayVO")
                        .resource(uuid, VpnGatewayVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
