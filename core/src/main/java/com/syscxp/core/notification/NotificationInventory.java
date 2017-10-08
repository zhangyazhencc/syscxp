package com.syscxp.core.notification;

import com.syscxp.header.message.NoJsonSchema;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by xing5 on 2017/3/18.
 */
@Inventory(mappingVOClass = NotificationVO.class)
public class NotificationInventory {
    private String uuid;
    private String accountUuid;
    private String userUuid;
    private String name;
    private String content;
    private String category;
    private String remoteIp;
    private Boolean success;
    private Map msgfields;
    private String sender;
    private String status;
    private String resourceUuid;
    private String resourceType;
    private String type;
    private Long time;
    @NoJsonSchema
    private Map opaque;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static NotificationInventory valueOf(NotificationVO vo) {
        NotificationInventory inv = new NotificationInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setUserUuid(vo.getUserUuid());
        inv.setRemoteIp(vo.getRemoteIp());
        inv.setCategory(vo.getCategory());
        inv.setSuccess(vo.isSuccess());
        inv.setName(vo.getName());
        inv.setContent(vo.getContent());
        inv.setMsgfields(vo.getMsgfields());
        inv.setSender(vo.getSender());
        inv.setStatus(vo.getStatus().toString());
        inv.setResourceUuid(vo.getResourceUuid());
        inv.setResourceType(vo.getResourceType());
        inv.setType(vo.getType().toString());
        inv.setTime(vo.getTime());
        if (vo.getOpaque() != null) {
            inv.setOpaque(vo.getOpaque());
        }
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<NotificationInventory> valueOf(Collection<NotificationVO> vos) {
        List<NotificationInventory> invs = new ArrayList<>();
        for (NotificationVO vo : vos) {
            invs.add(valueOf(vo));
        }
        return invs;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Map getOpaque() {
        return opaque;
    }

    public void setOpaque(Map opaque) {
        this.opaque = opaque;
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
