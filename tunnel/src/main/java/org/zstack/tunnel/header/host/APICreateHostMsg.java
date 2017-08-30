package org.zstack.tunnel.header.host;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-30
 */
public class APICreateHostMsg extends APIMessage {

    @APIParam(nonempty = true,maxLength = 128)
    private String name;

    @APIParam(nonempty = true,maxLength = 128)
    private String code;

    @APIParam(nonempty = true,maxLength = 128)
    private String ip;

    @APIParam(nonempty = true,maxLength = 128)
    private String username;

    @APIParam(nonempty = true,maxLength = 128)
    private String password;

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

    @Override
    public String getIp() {
        return ip;
    }

    @Override
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
