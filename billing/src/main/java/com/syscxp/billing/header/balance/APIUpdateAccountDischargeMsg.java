package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"update"})
public class APIUpdateAccountDischargeMsg extends APIMessage{

    @APIParam(emptyString = false, resourceType = AccountDischargeVO.class)
    private String uuid;

    @APIParam(numberRange = {1, 100})
    private int discharge;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getDischarge() {
        return discharge;
    }

    public void setDischarge(int discharge) {
        this.discharge = discharge;
    }
}
