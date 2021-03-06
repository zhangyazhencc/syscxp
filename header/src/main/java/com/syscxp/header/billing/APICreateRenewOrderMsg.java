package com.syscxp.header.billing;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;

import java.sql.Timestamp;

@InnerCredentialCheck
public class APICreateRenewOrderMsg  extends APICreateOrderMsg {

    @APIParam(emptyString = false)
    private ProductChargeModel productChargeModel;

    @APIParam(numberRange = {1,Integer.MAX_VALUE})
    private int duration;

    @APIParam
    private Timestamp startTime;
    @APIParam
    private Timestamp expiredTime;

    @APIParam(required = false)
    private boolean isAutoRenew;

    public Timestamp getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Timestamp expiredTime) {
        this.expiredTime = expiredTime;
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


    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public APICreateRenewOrderMsg(APICreateOrderMsg msg) {
        super(msg);
    }

    public boolean isAutoRenew() {
        return isAutoRenew;
    }

    public void setAutoRenew(boolean autoRenew) {
        isAutoRenew = autoRenew;
    }

    public APICreateRenewOrderMsg() {
    }
}
