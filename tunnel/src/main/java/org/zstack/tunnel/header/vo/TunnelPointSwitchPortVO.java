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
public class TunnelPointSwitchPortVO {

    @Id
    @Column
    private String uuid;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = SwitchVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String switchUuid;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = SwitchPortVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String switchPortUuid;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = TunnelPointVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String tunnelPointUuid;

    @Column
    private String groupUuid;

    @Column
    private Integer portNum;

    @Column
    private String portName;

    @Column
    private String portType;

    @Column
    private Integer vlan;

    @Column
    private Integer innerVlan;

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

    public String getSwitchUuid() {
        return switchUuid;
    }

    public void setSwitchUuid(String switchUuid) {
        this.switchUuid = switchUuid;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public String getTunnelPointUuid() {
        return tunnelPointUuid;
    }

    public void setTunnelPointUuid(String tunnelPointUuid) {
        this.tunnelPointUuid = tunnelPointUuid;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
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

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public Integer getInnerVlan() {
        return innerVlan;
    }

    public void setInnerVlan(Integer innerVlan) {
        this.innerVlan = innerVlan;
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
