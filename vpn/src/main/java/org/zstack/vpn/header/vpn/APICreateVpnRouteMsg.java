package org.zstack.vpn.header.vpn;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
import org.zstack.vpn.manage.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"create"}, adminOnly = true)
public class APICreateVpnRouteMsg extends APIMessage {
    @APIParam(resourceType = VpnVO.class, checkAccount = true)
    private String gatewayUuid;
    @APIParam
    private RouteType routeType;
    @APIParam(emptyString = false)
    private String nextIface;
    @APIParam(emptyString = false)
    private String nextIface2;
    @APIParam(emptyString = false)
    private String targetCidr;

    public String getGatewayUuid() {
        return gatewayUuid;
    }

    public void setGatewayUuid(String gatewayUuid) {
        this.gatewayUuid = gatewayUuid;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public String getNextIface() {
        return nextIface;
    }

    public void setNextIface(String nextIface) {
        this.nextIface = nextIface;
    }

    public String getNextIface2() {
        return nextIface2;
    }

    public void setNextIface2(String nextIface2) {
        this.nextIface2 = nextIface2;
    }

    public String getTargetCidr() {
        return targetCidr;
    }

    public void setTargetCidr(String targetCidr) {
        this.targetCidr = targetCidr;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateVpnRouteEvent) evt).getInventory().getUuid();
                }

                ntfy("Create VpnRouteVO")
                        .resource(uuid, VpnRouteVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
