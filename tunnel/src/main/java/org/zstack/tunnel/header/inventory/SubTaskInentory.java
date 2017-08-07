package org.zstack.tunnel.header.inventory;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.SubTaskVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = SubTaskInentory.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "task", inventoryClass = TaskInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "taskUuid"),
        @ExpandedQuery(expandedField = "agent", inventoryClass = AgentInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "agentUuid", hidden = true)
})
public class SubTaskInentory {

    private String uuid;
    private String taskUuid;
    private String agentUuid;
    private String name;
    private Integer seq;
    private String body;
    private String result;
    private String status;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SubTaskInentory valueOf(SubTaskVO vo){
        SubTaskInentory inv = new SubTaskInentory();
        inv.setUuid(vo.getUuid());
        inv.setTaskUuid(vo.getTaskUuid());
        inv.setAgentUuid(vo.getAgentUuid());
        inv.setName(vo.getName());
        inv.setSeq(vo.getSeq());
        inv.setBody(vo.getBody());
        inv.setResult(vo.getResult());
        inv.setStatus(vo.getStatus());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }

    public String getAgentUuid() {
        return agentUuid;
    }

    public void setAgentUuid(String agentUuid) {
        this.agentUuid = agentUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
