package com.syscxp.tunnel.header.host;

import com.syscxp.header.host.APIAddHostEvent;
import com.syscxp.header.host.APIAddHostMsg;
import com.syscxp.header.host.HostConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.vo.NoView;
import com.syscxp.tunnel.header.node.NodeVO;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Action(category = HostConstant.ACTION_CATEGORY, adminOnly = true)
public class APICreateMonitorHostMsg extends APIAddHostMsg {
    @APIParam(emptyString = false, resourceType = NodeVO.class)
    private String nodeUuid;

    @APIParam(emptyString = false)
    private String username;
    @APIParam(emptyString = false)
    private String password;
    @APIParam
    private Integer sshPort;

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
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

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APIAddHostEvent) evt).getInventory().getUuid();
                }
                ntfy("CreateMonitorHost")
                        .resource(uuid, MonitorHostVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
