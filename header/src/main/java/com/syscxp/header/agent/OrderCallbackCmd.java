package com.syscxp.header.agent;

import com.syscxp.header.billing.OrderVO;
import com.syscxp.header.billing.OrderType;
import com.syscxp.header.billing.ProductType;

import java.sql.Timestamp;

public class OrderCallbackCmd {
    private String porductUuid;
    private ProductType productType;
    private OrderType type;
    private Timestamp expireDate;

    public static OrderCallbackCmd valueOf(OrderVO order){
        OrderCallbackCmd cmd = new OrderCallbackCmd();
        cmd.setPorductUuid(order.getProductUuid());
        cmd.setType(order.getType());
        cmd.setProductType(order.getProductType());
        cmd.setExpireDate(order.getProductEffectTimeEnd());
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
}
