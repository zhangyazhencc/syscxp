package com.syscxp.core.notification;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.util.Map;

@InnerCredentialCheck
public class APICreateNotificationMsg extends APIMessage {
    @APIParam
    private String name;
    @APIParam
    private String category;
    @APIParam(required = false)
    private String accountUuid;
    @APIParam(required = false)
    private String opAccountUuid;
    @APIParam(required = false)
    private String opUserUuid;
    @APIParam
    private String content;
    @APIParam
    private String msgfields;
    @APIParam
    private String sender;
    @APIParam
    private String remoteIp;
    @APIParam
    private Boolean success;
    @APIParam(required = false)
    private String resourceUuid;
    @APIParam
    private String resourceType;
    @APIParam
    private NotificationType type;
    @APIParam
    private Map opaque;

    public String getOpUserUuid() {
        return opUserUuid;
    }

    public void setOpUserUuid(String opUserUuid) {
        this.opUserUuid = opUserUuid;
    }

    public String getAccountUuid() {

        return accountUuid;
    }

    public String getOpAccountUuid() {
        return opAccountUuid;
    }

    public void setOpAccountUuid(String opAccountUuid) {
        this.opAccountUuid = opAccountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public Map getOpaque() {
        return opaque;
    }

    public void setOpaque(Map opaque) {
        this.opaque = opaque;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgfields() {
        return msgfields;
    }

    public void setMsgfields(String msgfields) {
        this.msgfields = msgfields;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}
