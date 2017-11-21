package com.syscxp.header.vpn.host;

import com.syscxp.header.host.APIAddHostMsg;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.vpn.VpnConstant;

public class APICreateVpnHostMsg extends APIAddHostMsg {
    @APIParam(emptyString = false)
    private String publicInterface;
    @APIParam(emptyString = false)
    private String publicIp;
    @APIParam(emptyString = false, resourceType = ZoneVO.class)
    private String zoneUuid;
    @APIParam
    private Integer sshPort;
    @APIParam(emptyString = false)
    private String username;
    @APIParam(emptyString = false)
    private String password;
    @APIParam(emptyString = false)
    private String vpnInterfaceName;

    public String getVpnInterfaceName() {
        return vpnInterfaceName;
    }

    public void setVpnInterfaceName(String vpnInterfaceName) {
        this.vpnInterfaceName = vpnInterfaceName;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public String getPublicInterface() {
        return publicInterface;
    }

    public void setPublicInterface(String publicInterface) {
        this.publicInterface = publicInterface;
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
                    uuid = ((APICreateVpnHostEvent) evt).getInventory().getUuid();
                }

                ntfy("Create VpnHostVO")
                        .resource(uuid, VpnHostVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
