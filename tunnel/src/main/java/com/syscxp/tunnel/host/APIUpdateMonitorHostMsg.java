package com.syscxp.tunnel.host;

import com.syscxp.header.host.APIUpdateHostMsg;
import com.syscxp.header.host.HostConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.node.NodeVO;

@Action(category = HostConstant.ACTION_CATEGORY, adminOnly = true)
public class APIUpdateMonitorHostMsg extends APIUpdateHostMsg {

    @APIParam(required = false, emptyString = false, resourceType = NodeVO.class)
    private String nodeUuid;
    @APIParam(maxLength = 255, required = false, emptyString = false)
    private String username;
    @APIParam(maxLength = 255, required = false, emptyString = false)
    private String password;
    @APIParam(numberRange = {1, 65535})
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


}
