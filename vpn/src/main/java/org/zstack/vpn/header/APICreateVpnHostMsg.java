package org.zstack.vpn.header;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
import org.zstack.vpn.manage.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"create"}, adminOnly = true)
public class APICreateVpnHostMsg extends APIMessage {
    @APIParam(emptyString = false)
    private String name;
    @APIParam(required = false)
    private String description;
    @APIParam(emptyString = false)
    private String publicIface;
    @APIParam(emptyString = false)
    private String tunnelIface;
    @APIParam(emptyString = false)
    private String hostIp;
    @APIParam(emptyString = false)
    private String sshPort;
    @APIParam(emptyString = false)
    private String username;
    @APIParam(emptyString = false)
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublicIface() {
        return publicIface;
    }

    public void setPublicIface(String publicIface) {
        this.publicIface = publicIface;
    }

    public String getTunnelIface() {
        return tunnelIface;
    }

    public void setTunnelIface(String tunnelIface) {
        this.tunnelIface = tunnelIface;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getSshPort() {
        return sshPort;
    }

    public void setSshPort(String sshPort) {
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
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateVpnHostEvent) evt).getInventory().getUuid();
                }

                ntfy("Create VpnHostVO")
                        .resource(uuid, VpnHostVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
