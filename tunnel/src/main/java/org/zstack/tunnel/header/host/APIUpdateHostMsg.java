package org.zstack.tunnel.header.host;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-08-30
 */
@Action(category = TunnelConstant.ACTION_CATEGORY_HOST, names = {"update"}, adminOnly = true)
public class APIUpdateHostMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = HostVO.class)
    private String uuid;

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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
