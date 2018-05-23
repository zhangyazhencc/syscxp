package com.syscxp.header.tunnel.cloudhub;


import com.syscxp.header.tunnel.tunnel.TunnelType;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class CloudHubOfferingVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String name;

    @Column
    @Enumerated(value = EnumType.STRING)
    private TunnelType area;

    @Column
    private Integer number;

    @Column
    private Long bandwidth;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TunnelType getArea() {
        return area;
    }

    public void setArea(TunnelType area) {
        this.area = area;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
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
}
