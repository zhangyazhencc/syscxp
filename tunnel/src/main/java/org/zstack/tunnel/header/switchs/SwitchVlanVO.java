package org.zstack.tunnel.header.switchs;

import org.zstack.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-29
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class SwitchVlanVO {
    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = SwitchEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String switchUuid;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="switchUuid", insertable=false, updatable=false)
    private SwitchVO switchs;

    @Column
    private Integer startVlan;

    @Column
    private Integer endVlan;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

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

    public Integer getStartVlan() {
        return startVlan;
    }

    public void setStartVlan(Integer startVlan) {
        this.startVlan = startVlan;
    }

    public Integer getEndVlan() {
        return endVlan;
    }

    public void setEndVlan(Integer endVlan) {
        this.endVlan = endVlan;
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

    public SwitchVO getSwitchs() {
        return switchs;
    }

    public void setSwitchs(SwitchVO switchs) {
        this.switchs = switchs;
    }
}
