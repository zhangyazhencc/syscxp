package com.syscxp.header.billing;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;

import java.sql.Timestamp;
import java.util.List;

@Action(category = BillingConstant.ACTION_CATEGORY_ORDER)
@InnerCredentialCheck
public class APICreateModifyOrderMsg extends APICreateOrderMsg {

    @APIParam
    private Timestamp startTime;
    @APIParam
    private Timestamp expiredTime;

    public Timestamp getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Timestamp expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }


}
