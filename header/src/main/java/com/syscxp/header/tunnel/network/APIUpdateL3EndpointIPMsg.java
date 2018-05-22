package com.syscxp.header.tunnel.network;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/3/12
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = L3NetWorkConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateL3EndpointIPMsg extends APIMessage {
    @APIParam(emptyString = false, resourceType = L3EndpointVO.class)
    private String uuid;

    @APIParam(emptyString = false)
    private String localIP;

    @APIParam(emptyString = false)
    private String remoteIp;

    @APIParam(emptyString = false,required = false)
    private String monitorIp;

    @APIParam(emptyString = false)
    private String netmask;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getMonitorIp() {
        return monitorIp;
    }

    public void setMonitorIp(String monitorIp) {
        this.monitorIp = monitorIp;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update L3EndpointIP")
                        .resource(uuid, L3EndpointVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
