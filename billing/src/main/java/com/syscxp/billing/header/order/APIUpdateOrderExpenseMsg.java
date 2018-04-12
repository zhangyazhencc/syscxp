package com.syscxp.billing.header.order;

import com.syscxp.billing.header.renew.RenewVO;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.billing.OrderVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.math.BigDecimal;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_ORDER, adminOnly = true)
public class APIUpdateOrderExpenseMsg extends APIMessage {

    @APIParam
    private BigDecimal newPrice;

    @APIParam(emptyString = false,resourceType = RenewVO.class)
    private String renewUuid;

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public String getRenewUuid() {
        return renewUuid;
    }

    public void setRenewUuid(String renewUuid) {
        this.renewUuid = renewUuid;
    }
}
