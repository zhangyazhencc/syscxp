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
public class SwitchPortVO {

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
    private Integer portNum;

    @Column
    private String portName;

    @Column
    @Enumerated(EnumType.STRING)
    private SwitchPortLabel label;

    @Column
    private Integer reuse;

    @Column
    private Integer autoAlloc;

    @Column
    private Integer enabled;

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

    public SwitchPortLabel getLabel() {
        return label;
    }

    public void setLabel(SwitchPortLabel label) {
        this.label = label;
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
