package com.syscxp.header.billing;

import com.syscxp.header.billing.ProductCategoryVO;
import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger(foreignVOClass=ProductCategoryVO.class,foreignVOJoinColumn="productCategoryUuid")
public class AccountDiscountVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String accountUuid;

    @Column
    private String productCategoryUuid;

    @OneToOne(fetch = FetchType.EAGER,mappedBy = "accountDiscountVO")
//    @JoinColumn(name="productCategoryUuid",insertable = false,updatable = false)
    private ProductCategoryVO productCategoryEO;

    @Column
    private int discount;

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

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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

    public String getProductCategoryUuid() {
        return productCategoryUuid;
    }

    public void setProductCategoryUuid(String productCategoryUuid) {
        this.productCategoryUuid = productCategoryUuid;
    }

    public ProductCategoryVO getProductCategoryVO() {
        return productCategoryEO;
    }

    public void setProductCategoryVO(ProductCategoryVO productCategoryEO) {
        this.productCategoryEO = productCategoryEO;
    }
}
