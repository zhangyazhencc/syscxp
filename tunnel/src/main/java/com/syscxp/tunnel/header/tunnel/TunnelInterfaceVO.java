package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-18
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class TunnelInterfaceVO {
    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = TunnelEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String tunnelUuid;

    @Column
    @ForeignKey(parentEntityClass = InterfaceEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String interfaceUuid;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="interfaceUuid", insertable=false, updatable=false)
    private InterfaceVO interfaceVO;

    @Column
    private Integer vlan;

    @Column
    private String sortTag;

    @Column
    @Enumerated(EnumType.STRING)
    private TunnelQinqState qinqState;

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

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public TunnelQinqState getQinqState() {
        return qinqState;
    }

    public void setQinqState(TunnelQinqState qinqState) {
        this.qinqState = qinqState;
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

    public String getSortTag() {
        return sortTag;
    }

    public void setSortTag(String sortTag) {
        this.sortTag = sortTag;
    }

    public InterfaceVO getInterfaceVO() {
        return interfaceVO;
    }

    public void setInterfaceVO(InterfaceVO interfaceVO) {
        this.interfaceVO = interfaceVO;
    }
}
