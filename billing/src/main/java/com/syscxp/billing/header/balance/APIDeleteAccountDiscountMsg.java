package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountDiscountVO;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT)
public class APIDeleteAccountDiscountMsg extends APIMessage{

    @APIParam(emptyString = false,resourceType = AccountDiscountVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
