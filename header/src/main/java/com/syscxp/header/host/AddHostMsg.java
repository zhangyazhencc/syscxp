package com.syscxp.header.host;

import com.syscxp.header.message.NeedReplyMessage;

public class AddHostMsg extends NeedReplyMessage implements AddHostMessage {
    private String name;
    private String code;
    private String hostIp;
    private String position;
    private String accountUuid;
    private String resourceUuid;
    private String hostType;

    public AddHostMsg() {
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    @Override
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    @Override
    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    @Override
    public String getHostType() {
        return hostType;
    }

    public void setHostType(String hostType) {
        this.hostType = hostType;
    }

    public static AddHostMsg valueOf(final APIAddHostMsg msg) {
        AddHostMsg amsg = new AddHostMsg();

        amsg.setAccountUuid(msg.getSession().getAccountUuid());
        amsg.setName(msg.getName());
        amsg.setCode(msg.getCode());
        amsg.setHostIp(msg.getHostIp());
        amsg.setPosition(msg.getPosition());
        amsg.setResourceUuid(msg.getResourceUuid());
        amsg.setHostType(msg.getHostType());
        return amsg;
    }
}
