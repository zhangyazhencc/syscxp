package com.syscxp.header.account;


import com.syscxp.header.message.APIReply;

public class APIGetAccountInfoReply extends APIReply{

    private String uuid;

    private String name;

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
}
