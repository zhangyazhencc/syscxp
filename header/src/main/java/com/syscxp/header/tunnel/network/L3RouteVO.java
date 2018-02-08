package com.syscxp.header.tunnel.network;

import com.syscxp.header.vo.ForeignKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table
public class L3RouteVO {

    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = L3EndPointVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String l3EndPointUuid;

    @Column
    private String cidr;

    @Column
    private String nextIp;

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

    public String getL3EndPointUuid() {
        return l3EndPointUuid;
    }

    public void setL3EndPointUuid(String l3EndPointUuid) {
        this.l3EndPointUuid = l3EndPointUuid;
    }

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public String getNextIp() {
        return nextIp;
    }

    public void setNextIp(String nextIp) {
        this.nextIp = nextIp;
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
