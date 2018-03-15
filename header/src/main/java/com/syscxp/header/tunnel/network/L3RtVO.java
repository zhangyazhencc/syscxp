package com.syscxp.header.tunnel.network;


import com.syscxp.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class L3RtVO {

    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = L3EndPointVO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String l3EndPointUuid;

    @Column
    private String impor;

    @Column
    private String export;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

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

    public String getL3EndPointUuid() {
        return l3EndPointUuid;
    }

    public void setL3EndPointUuid(String l3EndPointUuid) {
        this.l3EndPointUuid = l3EndPointUuid;
    }

    public String getImpor() {
        return impor;
    }

    public void setImpor(String impor) {
        this.impor = impor;
    }

    public String getExport() {
        return export;
    }

    public void setExport(String export) {
        this.export = export;
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