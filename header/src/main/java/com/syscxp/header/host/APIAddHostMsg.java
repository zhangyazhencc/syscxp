package com.syscxp.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APICreateMessage;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.APINoSee;

import javax.persistence.Column;

@Action(category = HostConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public abstract class APIAddHostMsg extends APICreateMessage implements AddHostMessage {

    @APIParam(maxLength = 255)
    private String name;
    @APIParam(emptyString = false)
    private String code;
    @APIParam(emptyString = false)
    private String hostIp;
    @APINoSee
    private String hostType;
    @APIParam
    private String position;
    public String getHostType() {
        return hostType;
    }

    public void setHostType(String hostType) {
        this.hostType = hostType;
    }

    public APIAddHostMsg() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

}