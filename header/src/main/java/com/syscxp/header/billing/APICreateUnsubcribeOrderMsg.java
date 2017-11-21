package com.syscxp.header.billing;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.identity.Action;

import java.sql.Timestamp;

@InnerCredentialCheck
@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_ORDER)
public class APICreateUnsubcribeOrderMsg extends APICreateOrderMsg {

    @APIParam
    private Timestamp startTime;
    @APIParam
    private Timestamp expiredTime;

    @APIParam(required = false)
    private boolean isCreateFailure;

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

    public APICreateUnsubcribeOrderMsg(APICreateOrderMsg msg) {
        super(msg);
    }

    public APICreateUnsubcribeOrderMsg() {
    }

    public boolean isCreateFailure() {
        return isCreateFailure;
    }

    public void setCreateFailure(boolean createFailure) {
        isCreateFailure = createFailure;
    }
}
