package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.vo.ForeignKey;

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
    private String portType;

    @Column
    private String portAttribute;

    @Column
    private Integer autoAllot;

    @Column
    @Enumerated(EnumType.STRING)
    private SwitchPortState state;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

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

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public SwitchPortState getState() {
        return state;
    }

    public void setState(SwitchPortState state) {
        this.state = state;
    }

    public Integer getAutoAllot() {
        return autoAllot;
    }

    public void setAutoAllot(Integer autoAllot) {
        this.autoAllot = autoAllot;
    }

    public String getPortAttribute() {
        return portAttribute;
    }

    public void setPortAttribute(String portAttribute) {
        this.portAttribute = portAttribute;
    }
}
