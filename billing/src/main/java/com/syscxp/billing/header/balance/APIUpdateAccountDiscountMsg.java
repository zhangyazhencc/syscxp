package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"update"})
public class APIUpdateAccountDiscountMsg extends APIMessage{

    @APIParam(emptyString = false, resourceType = AccountDiscountVO.class)
    private String uuid;

    @APIParam(numberRange = {1, 100})
    private int discount;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
