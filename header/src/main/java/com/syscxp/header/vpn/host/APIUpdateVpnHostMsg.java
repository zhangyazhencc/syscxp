package com.syscxp.header.vpn.host;

import com.syscxp.header.host.APIUpdateHostMsg;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.vpn.VpnConstant;

public class APIUpdateVpnHostMsg extends APIUpdateHostMsg {
    @APIParam(resourceType = VpnHostVO.class)
    private String uuid;
    @APIParam(required = false)
    private String publicInterface;
    @APIParam(required = false)
    private String publicIp;
    @APIParam(required = false)
    private Integer sshPort;
    @APIParam(required = false)
    private String username;
    @APIParam(required = false)
    private String password;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
                ntfy("Update VpnHostVO")
                        .resource(uuid, VpnHostVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
