package com.syscxp.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.vpn.VpnConstant;

@Action(services = {TunnelConstant.ACTION_SERVICE, VpnConstant.ACTION_SERVICE}, category = HostConstant.ACTION_CATEGORY, adminOnly = true)
public class APIUpdateHostMsg extends APIMessage implements HostMessage {
    @APIParam(resourceType = HostVO.class)
    private String uuid;
    @APIParam(maxLength = 255, required = false)
    private String name;
    @APIParam(emptyString = false, maxLength = 128)
    private String code;
    @APIParam(maxLength = 255, required = false, emptyString = false)
    private String hostIp;
    @APIParam(required = false, maxLength = 256)
    private String position;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

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

    @Override
    public String getHostUuid() {
        return uuid;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Updated").resource(uuid, HostVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
