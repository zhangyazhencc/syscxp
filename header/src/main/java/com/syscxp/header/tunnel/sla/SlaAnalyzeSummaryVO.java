package com.syscxp.header.tunnel.sla;

import com.syscxp.header.billing.ProductType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-22.
 * @Description: SLA分析汇总.
 */
@Entity
public class SlaAnalyzeSummaryVO {
    @Id
    @Column
    private String uuid;

    @Column
    private Integer batchNum;

    @Column
    private String productUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    private long standardServiceDuration;

    @Column
    private long unnormalDuration;

    @Column
    private Double availableRate;

    @Column
    private Double unnormalRate;

    @Column
    private Boolean isSuccess;

    @Column
    private long start;

    @Column
    private long end;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name = "summaryUuid", insertable = false, updatable = false)
    private List<SlaAnalyzeVO> slaAnalyzeVOS = new ArrayList<>();

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

    public Integer getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Integer batchNum) {
        this.batchNum = batchNum;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public long getStandardServiceDuration() {
        return standardServiceDuration;
    }

    public void setStandardServiceDuration(long standardServiceDuration) {
        this.standardServiceDuration = standardServiceDuration;
    }

    public long getUnnormalDuration() {
        return unnormalDuration;
    }

    public void setUnnormalDuration(long unnormalDuration) {
        this.unnormalDuration = unnormalDuration;
    }

    public Double getAvailableRate() {
        return availableRate;
    }

    public void setAvailableRate(Double availableRate) {
        this.availableRate = availableRate;
    }

    public Double getUnnormalRate() {
        return unnormalRate;
    }

    public void setUnnormalRate(Double unnormalRate) {
        this.unnormalRate = unnormalRate;
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

    public List<SlaAnalyzeVO> getSlaAnalyzeVOS() {
        return slaAnalyzeVOS;
    }

    public void setSlaAnalyzeVOS(List<SlaAnalyzeVO> slaAnalyzeVOS) {
        this.slaAnalyzeVOS = slaAnalyzeVOS;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
