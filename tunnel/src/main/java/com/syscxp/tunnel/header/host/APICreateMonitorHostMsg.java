package com.syscxp.tunnel.header.host;

import com.syscxp.header.host.APIAddHostMsg;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.node.NodeVO;

/**
 * Created by DCY on 2017-08-30
 */

public class APICreateMonitorHostMsg extends APIAddHostMsg {
    @APIParam(emptyString = false,resourceType = NodeVO.class)
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
}
