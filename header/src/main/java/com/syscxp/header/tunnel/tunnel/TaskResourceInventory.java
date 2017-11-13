package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2017/11/8
 */
@Inventory(mappingVOClass = TaskResourceVO.class)
public class TaskResourceInventory {

    private String uuid;
    private String accountUuid;
    private String resourceUuid;
    private String resourceType;
    private TaskType taskType;
    private String body;
    private String result;
    private TaskStatus status;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static TaskResourceInventory valueOf(TaskResourceVO vo){
        TaskResourceInventory inv = new TaskResourceInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setResourceUuid(vo.getResourceUuid());
        inv.setResourceType(vo.getResourceType());
        inv.setTaskType(vo.getTaskType());
        inv.setBody(vo.getBody());
        inv.setResult(vo.getResult());
        inv.setStatus(vo.getStatus());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<TaskResourceInventory> valueOf(Collection<TaskResourceVO> vos) {
        List<TaskResourceInventory> lst = new ArrayList<TaskResourceInventory>(vos.size());
        for (TaskResourceVO vo : vos) {
            lst.add(TaskResourceInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
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

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
