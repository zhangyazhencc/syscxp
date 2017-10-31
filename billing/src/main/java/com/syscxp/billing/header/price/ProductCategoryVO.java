package com.syscxp.billing.header.price;

import com.syscxp.billing.header.balance.AccountDiscountVO;
import com.syscxp.header.billing.Category;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;
import com.syscxp.header.vo.EO;
import com.syscxp.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

@Table
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger(foreignVOClass = AccountDiscountVO.class, foreignVOJoinColumn = "uuid")
public class ProductCategoryVO{

    @Id
    @Column
    private String uuid;

    @Column
    @Enumerated(EnumType.STRING)
    private Category code;

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

    public Category getCode() {
        return code;
    }

    public void setCode(Category code) {
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
