package org.zstack.tunnel.header.host;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-08-30
 */

@Action(category = TunnelConstant.ACTION_CATEGORY_HOST, names = {"create"}, adminOnly = true)
public class APICreateHostMsg extends APIMessage {
    @APIParam(emptyString = false,maxLength = 32)
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
