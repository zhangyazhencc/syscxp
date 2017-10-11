package com.syscxp.tunnel.header.host;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.node.NodeVO;

/**
 * Created by DCY on 2017-08-30
 */

public class APICreateHostMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = NodeVO.class)
    private String nodeUuid;

    @APIParam(emptyString = false,maxLength = 128)
    private String name;

    @APIParam(emptyString = false,maxLength = 128)
    private String code;

    @APIParam(emptyString = false,maxLength = 128)
    private String hostIp;

    @APIParam(emptyString = false,maxLength = 128)
    private String username;

    @APIParam(emptyString = false,maxLength = 128)
    private String password;

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
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
}