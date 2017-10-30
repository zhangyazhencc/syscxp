package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.Category;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class AccountDiscountVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String accountUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column
    private int discount;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @Column
    private String categoryName;

    @Column
    private String productTypeName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = Math.abs(discount);
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

    @PreUpdate
    void preUpdate() {
        lastOpDate = null;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
