package com.syscxp.header.billing;

import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class ProductPriceUnitVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String productCategoryUuid;

    @Column
    private String areaCode;

    @Column
    private String areaName;

    @Column
    private String lineCode;

    @Column
    private String lineName;

    @Column
    private String configCode;

    @Column
    private String configName;

    @Column
    private int unitPrice;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @OneToOne
    @JoinColumn(name="productCategoryUuid",referencedColumnName = "uuid")
    private ProductCategoryVO productCategoryVO;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProductCategoryUuid() {
        return productCategoryUuid;
    }

    public void setProductCategoryUuid(String productCategoryUuid) {
        this.productCategoryUuid = productCategoryUuid;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
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

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public ProductCategoryVO getProductCategoryVO() {
        return productCategoryVO;
    }

    public void setProductCategoryVO(ProductCategoryVO productCategoryVO) {
        this.productCategoryVO = productCategoryVO;
    }

    @PreUpdate
    void preUpdate() {
        lastOpDate = null;
    }
}
