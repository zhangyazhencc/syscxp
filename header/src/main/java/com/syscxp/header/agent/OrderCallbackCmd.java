package com.syscxp.header.agent;

import com.syscxp.header.billing.OrderVO;
import com.syscxp.header.billing.OrderType;
import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.billing.ProductType;

import java.sql.Timestamp;

public class OrderCallbackCmd {
    private String porductUuid;
    private ProductType productType;
    private OrderType type;
    private Timestamp expireDate;
    private int duration;
    private ProductChargeModel productChargeModel;
    private String productDescription;

    public static OrderCallbackCmd valueOf(OrderVO order){
        OrderCallbackCmd cmd = new OrderCallbackCmd();
        cmd.setPorductUuid(order.getProductUuid());
        cmd.setType(order.getType());
        cmd.setProductType(order.getProductType());
        cmd.setExpireDate(order.getProductEffectTimeEnd());
        cmd.setDuration(order.getDuration());
        cmd.setProductChargeModel(order.getProductChargeModel());
        cmd.setProductDescription(order.getProductDescription());
        return cmd;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getPorductUuid() {
        return porductUuid;
    }

    public void setPorductUuid(String porductUuid) {
        this.porductUuid = porductUuid;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
