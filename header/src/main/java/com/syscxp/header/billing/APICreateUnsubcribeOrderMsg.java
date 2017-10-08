package com.syscxp.header.billing;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.identity.Action;

import java.sql.Timestamp;

@InnerCredentialCheck
@Action(category = BillingConstant.ACTION_CATEGORY_ORDER)
public class APICreateUnsubcribeOrderMsg extends APICreateOrderMsg {

    @APIParam(emptyString = false)
    private ProductType productType;

    @APIParam(emptyString = false)
    private String productUuid;

    @APIParam(emptyString = false)
    private String productName;

    @APIParam
    private Timestamp startTime;
    @APIParam
    private Timestamp expiredTime;

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Timestamp expiredTime) {
        this.expiredTime = expiredTime;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
