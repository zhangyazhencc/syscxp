package com.syscxp.header.tunnel.sla;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-07.
 * @Description: SLA分析结果.
 */
@Entity
public class SlaAnalyzeVO {
    @Id
    @Column
    private String uuid;

    @Column
    private Integer batchNum;

    @Column
    private String summaryUuid;

    @Column
    private String endpoint;

    @Column
    private Integer vlan;

    @Column
    private String metric;

    @Column
    @Enumerated(EnumType.STRING)
    private SlaLevel level;

    @Column
    private Timestamp start;

    @Column
    private Timestamp end;

    @Column
    private long duration;

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

    public String getSummaryUuid() {
        return summaryUuid;
    }

    public void setSummaryUuid(String summaryUuid) {
        this.summaryUuid = summaryUuid;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public SlaLevel getLevel() {
        return level;
    }

    public void setLevel(SlaLevel level) {
        this.level = level;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public Integer getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Integer batchNum) {
        this.batchNum = batchNum;
    }
}
