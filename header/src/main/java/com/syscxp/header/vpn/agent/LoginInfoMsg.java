package com.syscxp.header.vpn.agent;

public class LoginInfoMsg extends VpnMessage {
    public String username;
    public String passwd;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}

