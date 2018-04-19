package com.syscxp.header.tunnel.network;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.TunnelConstant;

import java.util.List;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = L3NetWorkConstant.ACTION_CATEGORY, names = {"create"})
public class APICreateL3RouteMsg extends APIMessage {

    @APIParam(emptyString = false, resourceType = L3EndpointVO.class)
    private String l3EndpointUuid;
    @APIParam(emptyString = false)
    private String cidr;
    @APIParam(emptyString = false)
    private String routeIp;
    @APIParam(required = false)
    private List<L3SlaveRouteParam> l3SlaveRoute;

    public String getL3EndpointUuid() {
        return l3EndpointUuid;
    }

    public void setL3EndpointUuid(String l3EndpointUuid) {
        this.l3EndpointUuid = l3EndpointUuid;
    }

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public List<L3SlaveRouteParam> getL3SlaveRoute() {
        return l3SlaveRoute;
    }

    public void setL3SlaveRoute(List<L3SlaveRouteParam> l3SlaveRoute) {
        this.l3SlaveRoute = l3SlaveRoute;
    }

    public String getRouteIp() {
        return routeIp;
    }

    public void setRouteIp(String routeIp) {
        this.routeIp = routeIp;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateL3RouteEvent) evt).getInventory().getUuid();
                }
                ntfy("Create L3RouteVO")
                        .resource(uuid, L3RouteVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
