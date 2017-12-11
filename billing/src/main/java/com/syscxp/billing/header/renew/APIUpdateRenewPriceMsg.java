package com.syscxp.billing.header.renew;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(adminOnly = true,services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_RENEW, names = {"update"})
public class APIUpdateRenewPriceMsg extends APIMessage {

    @APIParam(emptyString = false, resourceType = RenewVO.class, checkAccount = true)
    private String uuid;

    @APIParam(numberRange = {1,Integer.MAX_VALUE})
    private Integer price;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
