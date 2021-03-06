package com.syscxp.core.notification;

import com.syscxp.header.core.converter.MapAttributeConverter;
import com.syscxp.header.vo.BaseResource;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by xing5 on 2017/3/15.
 */
@Entity
@Table
@BaseResource
public class NotificationVO {
    @Id
    @Column
    private String uuid;
    @Column
    private String accountUuid;
    @Column
    private String opAccountUuid;
    @Column
    private String opUserUuid;
    @Column
    private String name;
    @Column
    private String category;
    @Column
    private String content;
    @Column
    private String msgfields;
    @Column
    private String sender;
    @Column
    private String remoteIp;
    @Column
    private boolean success;
    @Column
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
    @Column
    private String resourceUuid;
    @Column
    private String resourceType;
    @Column
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @Column
    private long time;
    @Column
    @Convert(converter = MapAttributeConverter.class)
    private Map opaque;
    @Column
    private Timestamp createDate;
    @Column
    private Timestamp lastOpDate;

    public String getOpAccountUuid() {
        return opAccountUuid;
    }

    public void setOpAccountUuid(String opAccountUuid) {
        this.opAccountUuid = opAccountUuid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getOpUserUuid() {
        return opUserUuid;
    }

    public void setOpUserUuid(String opUserUuid) {
        this.opUserUuid = opUserUuid;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map getOpaque() {
        return opaque;
    }

    public void setOpaque(Map opaque) {
        this.opaque = opaque;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMsgfields() {
        return msgfields;
    }

    public void setMsgfields(String msgfields) {
        this.msgfields = msgfields;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
