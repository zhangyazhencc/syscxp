package com.syscxp.header.vpn.host;

import com.syscxp.header.host.APIAddHostEvent;
import com.syscxp.header.host.APIAddHostMsg;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

public class APICreateVpnHostMsg extends APIAddHostMsg {
    @APIParam(emptyString = false)
    private String publicIp;
    @APIParam(emptyString = false, resourceType = ZoneVO.class)
    private String zoneUuid;
    @APIParam(required = false)
    private Integer sshPort;
    @APIParam(emptyString = false)
    private String username;
    @APIParam(emptyString = false)
    private String password;

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getZoneUuid() {
        return zoneUuid;
    }

    public void setZoneUuid(String zoneUuid) {
        this.zoneUuid = zoneUuid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APIAddHostEvent) evt).getInventory().getUuid();
                }

                ntfy("Create VpnHostVO")
                        .resource(uuid, VpnHostVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
