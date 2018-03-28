package com.syscxp.header.billing;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.math.BigDecimal;

@InnerCredentialCheck
@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_ORDER)
public class APICreateBuyEdgeLineOrderMsg extends APISyncCallMessage {

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private int price;

    @APIParam(numberRange = {0, Integer.MAX_VALUE})
    private int fixedCost;

    @APIParam
    private String productName;

    @APIParam
    private String productUuid;

    @APIParam
    private String descriptionData;

    @APIParam
    private String callBackData;

    @APIParam
    private String accountUuid;

    @APIParam
    private String opAccountUuid;

    @APIParam(required = false)
    private String notifyUrl;

    @APIParam
    private ProductChargeModel productChargeModel;

    @APIParam(numberRange = {1,Integer.MAX_VALUE})
    private int duration;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public String getDescriptionData() {
        return descriptionData;
    }

    public void setDescriptionData(String descriptionData) {
        this.descriptionData = descriptionData;
    }

    public String getCallBackData() {
        return callBackData;
    }

    public void setCallBackData(String callBackData) {
        this.callBackData = callBackData;
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

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFixedCost() {
        return fixedCost;
    }

    public void setFixedCost(int fixedCost) {
        this.fixedCost = fixedCost;
    }
}
