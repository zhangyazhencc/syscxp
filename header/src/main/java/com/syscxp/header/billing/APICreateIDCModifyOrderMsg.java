package com.syscxp.header.billing;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.sql.Timestamp;
import java.util.List;

@InnerCredentialCheck
@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_ORDER)
public class APICreateIDCModifyOrderMsg extends APISyncCallMessage {

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private int fixedCost;

    @APIParam
    private Timestamp expiredTime;

    @APIParam(emptyString = false)
    private String productName;

    @APIParam(emptyString = false)
    private ProductType productType;

    @APIParam(emptyString = false)
    private String productUuid;

    @APIParam(emptyString = false)
    private String descriptionData;

    @APIParam(required = false)
    private String callBackData;

    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam(emptyString = false,required = false)
    private String opAccountUuid;

    @APIParam(emptyString = false)
    private String notifyUrl;

    public int getFixedCost() {
        return fixedCost;
    }

    public void setFixedCost(int fixedCost) {
        this.fixedCost = fixedCost;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
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

    public Timestamp getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Timestamp expiredTime) {
        this.expiredTime = expiredTime;
    }

    public APICreateIDCModifyOrderMsg(APICreateIDCModifyOrderMsg msg){
        this.productName = msg.getProductName();
        this.productType = msg.getProductType();
        this.productUuid = msg.getProductUuid();
        this.accountUuid = msg.getAccountUuid();
        this.opAccountUuid = msg.getOpAccountUuid();
        this.callBackData = msg.getCallBackData();
        this.descriptionData = msg.getDescriptionData();
        this.notifyUrl = msg.getNotifyUrl();
    }

    public APICreateIDCModifyOrderMsg(){}
}
