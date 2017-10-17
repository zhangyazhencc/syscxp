package com.syscxp.alarm.header.resourcePolicy;


import com.syscxp.alarm.header.BaseVO;
import com.syscxp.header.billing.ProductType;

import javax.persistence.*;

@Entity
@Table
public class ComparisonRuleVO extends BaseVO {

    @Column
    private String comparisonName;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    public String getComparisonName() {
        return comparisonName;
    }

    public void setComparisonName(String comparisonName) {
        this.comparisonName = comparisonName;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
}
