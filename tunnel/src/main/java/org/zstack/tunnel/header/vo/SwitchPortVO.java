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
public class SwitchPortVO {

    @Id
    @Column
    private String uuid;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = SwitchVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String switchtUuid;

    @Column
    private Integer portNum;

    @Column
    private String portName;

    @Column
    private String label;

    @Column
    private Integer vlan;

    @Column
    private Integer endVlan;

    @Column
    private Integer reuse;

    @Column
    private Integer autoAlloc;

    @Column
    private Integer enabled;

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

    public String getSwitchtUuid() {
        return switchtUuid;
    }

    public void setSwitchtUuid(String switchtUuid) {
        this.switchtUuid = switchtUuid;
    }

    public Integer getPortNum() {
        return portNum;
    }

    public void setPortNum(Integer portNum) {
        this.portNum = portNum;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public Integer getEndVlan() {
        return endVlan;
    }

    public void setEndVlan(Integer endVlan) {
        this.endVlan = endVlan;
    }

    public Integer getReuse() {
        return reuse;
    }

    public void setReuse(Integer reuse) {
        this.reuse = reuse;
    }

    public Integer getAutoAlloc() {
        return autoAlloc;
    }

    public void setAutoAlloc(Integer autoAlloc) {
        this.autoAlloc = autoAlloc;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
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
