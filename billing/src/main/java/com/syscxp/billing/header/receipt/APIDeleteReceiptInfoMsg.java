package com.syscxp.billing.header.receipt;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
@Action(category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"delete"})
public class APIDeleteReceiptInfoMsg  extends APIMessage {

    @APIParam(emptyString = false, resourceType = ReceiptInfoVO.class, checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
