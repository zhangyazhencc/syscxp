package org.zstack.core.notification;

import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIParam;

import java.util.List;
import java.util.Map;


public class APICreateNotificationMsg extends APICreateMessage {
    @APIParam
    private String name;
    @APIParam
    private String category;
    @APIParam
    private String content;
    @APIParam
    private List arguments;
    @APIParam
    private String sender;
    @APIParam
    private String action;
    @APIParam
    private Boolean success;
    @APIParam
    private String resourceUuid;
    @APIParam
    private String resourceType;
    @APIParam
    private NotificationType type;
    @APIParam
    private Map opaque;

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

    public List getArguments() {
        return arguments;
    }

    public void setArguments(List arguments) {
        this.arguments = arguments;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public String getResourceUuid() {
        return resourceUuid;
    }

    @Override
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
