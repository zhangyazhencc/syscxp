package com.syscxp.header.tunnel.node;

import com.syscxp.header.vo.NoView;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/11/1
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class ZoneNodeRefVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String nodeUuid;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nodeUuid", insertable = false, updatable = false)
    @NoView
    private NodeEO node;

    @Column
    private String zoneUuid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zoneUuid", insertable = false, updatable = false)
    @NoView
    private ZoneVO zone;

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

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public String getZoneUuid() {
        return zoneUuid;
    }

    public void setZoneUuid(String zoneUuid) {
        this.zoneUuid = zoneUuid;
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

    public NodeEO getNode() {
        return node;
    }

    public void setNode(NodeEO node) {
        this.node = node;
    }

    public ZoneVO getZone() {
        return zone;
    }

    public void setZone(ZoneVO zone) {
        this.zone = zone;
    }
}
