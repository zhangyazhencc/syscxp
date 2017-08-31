package org.zstack.tunnel.header.host;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-30
 */
public class APIUpdateHostMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = HostVO.class)
    private String targetUuid;

    @APIParam(required = false,maxLength = 128)
    private String name;

    @APIParam(required = false,maxLength = 128)
    private String code;

    @APIParam(required = false,maxLength = 128)
    private String ip;

    @APIParam(required = false,maxLength = 128)
    private String username;

    @APIParam(required = false,maxLength = 128)
    private String password;

    public String getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
