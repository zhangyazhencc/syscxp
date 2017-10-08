package com.syscxp.billing.header.renew;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY_RENEW, names = {"update"})
public class APIUpdateRenewMsg extends APIMessage {

    @APIParam()
    private boolean isRenewAuto;

    @APIParam(emptyString = false, resourceType = RenewVO.class, checkAccount = true)
    private String uuid;

    public boolean isRenewAuto() {
        return isRenewAuto;
    }

    public void setRenewAuto(boolean renewAuto) {
        isRenewAuto = renewAuto;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
