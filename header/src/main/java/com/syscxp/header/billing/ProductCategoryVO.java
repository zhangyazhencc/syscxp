package com.syscxp.header.billing;

import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;
import javax.persistence.*;
import java.sql.Timestamp;

@Table
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger(foreignVOClass = AccountDiscountVO.class, foreignVOJoinColumn = "uuid")
public class ProductCategoryVO {
    @Id
    @Column
    private String uuid;
    @Column
    @Enumerated(EnumType.STRING)
    private ProductCategory code;
    @Column
    private String name;
    @Column
    @Enumerated(EnumType.STRING)
    private ProductType productTypeCode;
    @Column
    private String productTypeName;
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

    public ProductCategory getCode() {
        return code;
    }

    public void setCode(ProductCategory code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductType getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(ProductType productTypeCode) {
        this.productTypeCode = productTypeCode;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
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
}
