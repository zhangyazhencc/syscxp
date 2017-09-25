package org.zstack.core.notification;

import org.zstack.header.identity.InnerCredentialCheck;
import org.zstack.header.message.*;

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
    private String userUuid;
    @APIParam
    private String content;
    @APIParam
    private Map msgfields;
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

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getAccountUuid() {

        return accountUuid;
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

    public Map getMsgfields() {
        return msgfields;
    }

    public void setMsgfields(Map msgfields) {
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
