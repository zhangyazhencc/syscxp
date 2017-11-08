package com.syscxp.alarm.header.log;

import com.syscxp.alarm.header.BaseVO;
import com.syscxp.header.billing.ProductType;

import javax.persistence.*;

@Entity
@Table
public class AlarmTemplateVO extends BaseVO{
    @Column
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    private String regulationType;

    @Column
    private String template;

    @Column
    private String status;

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getRegulationType() {
        return regulationType;
    }

    public void setRegulationType(String regulationType) {
        this.regulationType = regulationType;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
