package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.billing.OrderType;
import com.syscxp.header.billing.ProductChargeModel;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-10-22
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class ProductsLogVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String productUuid;

    @Column
    private String accountUuid;

    @Column
    private String opAccountUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductsStatue statue;

    @Column
    private Integer duration;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductChargeModel productChargeModel;

    @Column
    private Timestamp expireDate;

    @Column
    private Timestamp createDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getOpAccountUuid() {
        return opAccountUuid;
    }

    public void setOpAccountUuid(String opAccountUuid) {
        this.opAccountUuid = opAccountUuid;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public ProductsStatue getStatue() {
        return statue;
    }

    public void setStatue(ProductsStatue statue) {
        this.statue = statue;
    }
}
