package org.zstack.billing.header.renew;

import org.zstack.billing.header.balance.ProductChargeModel;
import org.zstack.billing.header.balance.ProductType;
import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class RenewVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String accountUuid;

    @Column
    private boolean isRenewAuto;

    @Column
    private String productUuid;

    @Column
    private String productName;

    @Column
    private ProductType productType;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductChargeModel productChargeModel;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @Column
    private String productDescription;

    @Column
    private BigDecimal pricePerDay;

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
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

    public boolean isRenewAuto() {
        return isRenewAuto;
    }

    public void setRenewAuto(boolean renewAuto) {
        isRenewAuto = renewAuto;
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

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
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

    @PreUpdate
    void preUpdate() {
        lastOpDate = null;
    }
}
