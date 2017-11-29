package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;

public class LoginInfoReply extends MessageReply {
    private String passwdfile;

    public String getPasswdfile() {
        return passwdfile;
    }

    public void setPasswdfile(String passwdfile) {
        this.passwdfile = passwdfile;
    }
}
