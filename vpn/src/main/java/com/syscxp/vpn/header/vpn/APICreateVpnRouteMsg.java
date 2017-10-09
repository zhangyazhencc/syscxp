package com.syscxp.vpn.header.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.vpn.vpn.VpnConstant;

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
