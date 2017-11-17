package com.syscxp.header.billing;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.sql.Timestamp;

public class APIGetUnscribeProductPriceDiffMsg extends APISyncCallMessage {

    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam(emptyString = false)
    private String productUuid;

    @APIParam
    private Timestamp expiredTime;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public Timestamp getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Timestamp expiredTime) {
        this.expiredTime = expiredTime;
    }
}
