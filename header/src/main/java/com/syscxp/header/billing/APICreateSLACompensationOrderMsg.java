package com.syscxp.header.billing;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.identity.Action;

import java.sql.Timestamp;

@InnerCredentialCheck
@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_ORDER)
public class APICreateSLACompensationOrderMsg extends APICreateOrderMsg {

    @APIParam
    private int duration;

    @APIParam
    private String slaUuid;

    @APIParam
    private Timestamp startTime;
    @APIParam
    private Timestamp expiredTime;

    public String getSlaUuid() {
        return slaUuid;
    }

    public void setSlaUuid(String slaUuid) {
        this.slaUuid = slaUuid;
    }

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public APICreateSLACompensationOrderMsg(APICreateOrderMsg msg) {
        super(msg);
    }

    public APICreateSLACompensationOrderMsg() {
    }
}
