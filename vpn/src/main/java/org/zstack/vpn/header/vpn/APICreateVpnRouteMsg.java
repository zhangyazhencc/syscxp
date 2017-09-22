package org.zstack.vpn.header.vpn;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
import org.zstack.vpn.manage.VpnConstant;

import java.util.List;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"create"}, adminOnly = true)
public class APICreateVpnRouteMsg extends APIMessage {
    @APIParam(resourceType = VpnVO.class, checkAccount = true)
    private String vpnUuid;
    @APIParam
    private RouteType routeType;
    @APIParam(emptyString = false)
    private List<String> nextIface;
    @APIParam(emptyString = false)
    private String targetCidr;

    public String getVpnUuid() {
        return vpnUuid;
    }

    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public List<String> getNextIface() {
        return nextIface;
    }

    public void setNextIface(List<String> nextIface) {
        this.nextIface = nextIface;
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
