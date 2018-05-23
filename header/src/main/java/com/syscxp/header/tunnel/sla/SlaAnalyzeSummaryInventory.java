package com.syscxp.header.tunnel.sla;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-22.
 * @Description: .
 */
@Inventory(mappingVOClass = SlaAnalyzeSummaryVO.class)
public class SlaAnalyzeSummaryInventory {
    private String uuid;
    private Integer batchNum;
    private String productUuid;
    private ProductType productType;
    private long standardServiceDuration;
    private long unnormalDuration;
    private Double availableRate;
    private Double unnormalRate;
    private List<SlaAnalyzeInventory> slaAnalyzeVOS = new ArrayList<>();


    public static SlaAnalyzeSummaryInventory valueOf(SlaAnalyzeSummaryVO vo) {
        SlaAnalyzeSummaryInventory inv = new SlaAnalyzeSummaryInventory();
        inv.setUuid(vo.getUuid());
        inv.setBatchNum(vo.getBatchNum());
        inv.setProductType(vo.getProductType());
        inv.setProductUuid(vo.getProductUuid());
        inv.setStandardServiceDuration(vo.getStandardServiceDuration());
        inv.setUnnormalDuration(vo.getUnnormalDuration());
        inv.setAvailableRate(vo.getAvailableRate());
        inv.setUnnormalRate(vo.getUnnormalRate());
        inv.setSlaAnalyzeVOS(SlaAnalyzeInventory.valueOf(vo.getSlaAnalyzeVOS()));

        return inv;
    }

    public static List<SlaAnalyzeSummaryInventory> valueOf(Collection<SlaAnalyzeSummaryVO> vos) {
        List<SlaAnalyzeSummaryInventory> lst = new ArrayList<SlaAnalyzeSummaryInventory>(vos.size());
        for (SlaAnalyzeSummaryVO vo : vos) {
            lst.add(SlaAnalyzeSummaryInventory.valueOf(vo));
        }
        return lst;
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

    public List<SlaAnalyzeInventory> getSlaAnalyzeVOS() {
        return slaAnalyzeVOS;
    }

    public void setSlaAnalyzeVOS(List<SlaAnalyzeInventory> slaAnalyzeVOS) {
        this.slaAnalyzeVOS = slaAnalyzeVOS;
    }
}
