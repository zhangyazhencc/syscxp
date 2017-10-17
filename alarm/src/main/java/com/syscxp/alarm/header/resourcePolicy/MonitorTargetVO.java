package com.syscxp.alarm.header.resourcePolicy;


import com.syscxp.alarm.header.BaseVO;
import com.syscxp.header.billing.ProductType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class MonitorTargetVO extends BaseVO{

    @Column
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    private String targetName;

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}
