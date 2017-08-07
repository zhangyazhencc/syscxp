package org.zstack.tunnel.header.inventory;

import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.TaskVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = TaskVO.class)
public class TaskInventory {

    private String uuid;
    private String name;
    private String status;
    private String objectUuid;
    private String objectType;
    private String body;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static TaskInventory valueOf(TaskVO vo) {
        TaskInventory inv = new TaskInventory();
        inv.setUuid(vo.getUuid());
        inv.setObjectUuid(vo.getObjectUuid());
        inv.setObjectType(vo.getObjectType());
        inv.setBody(vo.getBody());
        inv.setStatus(vo.getStatus());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObjectUuid() {
        return objectUuid;
    }

    public void setObjectUuid(String objectUuid) {
        this.objectUuid = objectUuid;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
