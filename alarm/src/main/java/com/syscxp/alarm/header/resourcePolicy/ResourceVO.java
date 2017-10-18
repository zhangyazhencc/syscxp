package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.BaseVO;
import com.syscxp.header.billing.ProductType;

import javax.persistence.*;
import java.sql.Timestamp;

@Table
@Entity
public class ResourceVO  extends BaseVO{

    @Column
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    private String productUuid;

    @Column
    private String productName;

    @Column
    private String description;

    @Column
    private String networkSegmentA;

    @Column
    private String networkSegmentB;

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNetworkSegmentA() {
        return networkSegmentA;
    }

    public void setNetworkSegmentA(String networkSegmentA) {
        this.networkSegmentA = networkSegmentA;
    }

    public String getNetworkSegmentB() {
        return networkSegmentB;
    }

    public void setNetworkSegmentB(String networkSegmentB) {
        this.networkSegmentB = networkSegmentB;
    }

}
