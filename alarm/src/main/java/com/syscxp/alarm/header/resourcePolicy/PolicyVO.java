package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.BaseVO;
import com.syscxp.header.billing.ProductType;

import javax.persistence.*;
import java.sql.Timestamp;

@Table
@Entity
public class PolicyVO extends BaseVO {

    @Column
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private int bindResources;

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBindResources() {
        return bindResources;
    }

    public void setBindResources(int bindResources) {
        this.bindResources = bindResources;
    }

}
