package org.zstack.tunnel.header.vo;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;
import org.zstack.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy= InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class TunnelPointVO {

    @Id
    @Column
    private String uuid;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = TunnelVO.class, parentKey = "uuid", onDeleteAction = org.zstack.header.vo.ForeignKey.ReferenceOption.CASCADE)
    private String tunnelUuid;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = AgentVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String agentUuid;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = SwitchVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String switchUuid;

    @Column
    private String hostingUuid;

    @Column
    private String bridgeName;

    @Column
    private Integer meter;

    @Column
    private Integer priority;

    @Column
    private String role;

    @Column
    private String hostingType;

    @Column
    private Integer isAttached;

    @Column
    private Integer deleted;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getAgentUuid() {
        return agentUuid;
    }

    public void setAgentUuid(String agentUuid) {
        this.agentUuid = agentUuid;
    }

    public String getSwitchUuid() {
        return switchUuid;
    }

    public void setSwitchUuid(String switchUuid) {
        this.switchUuid = switchUuid;
    }

    public String getHostingUuid() {
        return hostingUuid;
    }

    public void setHostingUuid(String hostingUuid) {
        this.hostingUuid = hostingUuid;
    }

    public String getBridgeName() {
        return bridgeName;
    }

    public void setBridgeName(String bridgeName) {
        this.bridgeName = bridgeName;
    }

    public Integer getMeter() {
        return meter;
    }

    public void setMeter(Integer meter) {
        this.meter = meter;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHostingType() {
        return hostingType;
    }

    public void setHostingType(String hostingType) {
        this.hostingType = hostingType;
    }

    public Integer getIsAttached() {
        return isAttached;
    }

    public void setIsAttached(Integer isAttached) {
        this.isAttached = isAttached;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
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
