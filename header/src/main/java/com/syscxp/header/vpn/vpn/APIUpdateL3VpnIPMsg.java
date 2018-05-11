package com.syscxp.header.vpn.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"update"})
public class APIUpdateL3VpnIPMsg extends APIMessage {
    @APIParam(resourceType = L3VpnVO.class, checkAccount = true)
    private String uuid;
    @APIParam(emptyString = false)
    private String startIp;
    @APIParam(emptyString = false)
    private String endIp;
    @APIParam(emptyString = false)
    private String netmask;
    @APIParam(emptyString = false)
    private String gateway;
    @APIParam(emptyString = false)
    private String remoteIp;
    @APIParam(emptyString = false)
    private String monitorIp;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStartIp() { return startIp;}

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getEndIp() { return endIp; }

    public void setEndIp(String endIp) { this.endIp = endIp; }

    public String getNetmask() { return netmask; }

    public void setNetmask(String netmask) { this.netmask = netmask; }

    public String getGateway() { return gateway; }

    public void setGateway(String gateway) { this.gateway = gateway; }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
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
                ntfy("Update L3VpnVO Route IP")
                        .resource(uuid, L3VpnVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
