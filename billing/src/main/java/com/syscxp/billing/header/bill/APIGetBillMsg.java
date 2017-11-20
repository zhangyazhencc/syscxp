package com.syscxp.billing.header.bill;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressUserCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@SuppressUserCredentialCheck
@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_BILLING, names = {"read"})
public class APIGetBillMsg extends APISyncCallMessage{

    @APIParam(emptyString = false, resourceType = BillVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
