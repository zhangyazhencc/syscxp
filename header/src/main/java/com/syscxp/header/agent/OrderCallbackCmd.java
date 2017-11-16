package com.syscxp.header.agent;

import com.syscxp.header.billing.*;
import com.syscxp.header.callBack.CallBackData;

import java.sql.Timestamp;

public class OrderCallbackCmd {
    private String orderUuid;
    private String porductUuid;
    private ProductType productType;
    private OrderType type;
    private Timestamp expireDate;
    private int duration;
    private ProductChargeModel productChargeModel;
    private DescriptionData descriptionData;
    private CallBackData callBackData;

    public static OrderCallbackCmd valueOf(OrderInventory order){
        OrderCallbackCmd cmd = new OrderCallbackCmd();
        cmd.setOrderUuid(order.getUuid());
        cmd.setPorductUuid(order.getProductUuid());
        cmd.setType(order.getType());
        cmd.setProductType(order.getProductType());
        cmd.setExpireDate(order.getProductEffectTimeEnd());
        cmd.setDuration(order.getDuration());
        cmd.setProductChargeModel(order.getProductChargeModel());
        cmd.setDescriptionData(order.getDescriptionData());
        cmd.setCallBackData(order.getCallBackData());
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

    public DescriptionData getDescriptionData() {
        return descriptionData;
    }

    public void setDescriptionData(DescriptionData descriptionData) {
        this.descriptionData = descriptionData;
    }

    public CallBackData getCallBackData() {
        return callBackData;
    }

    public void setCallBackData(CallBackData callBackData) {
        this.callBackData = callBackData;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }


}
