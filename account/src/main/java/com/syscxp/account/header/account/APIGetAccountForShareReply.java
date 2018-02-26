package com.syscxp.account.header.account;


import com.syscxp.header.message.APIReply;

public class APIGetAccountForShareReply extends APIReply {

    private String uuid;

    private String name;

    private String phone;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
