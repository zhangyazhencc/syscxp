package org.zstack.billing.header.renew;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"renew"}, accountOnly = true)
public class APIUpdateRenewMsg extends APIMessage {

    @APIParam(nonempty = true)
    private boolean isRenewAuto;

    @APIParam(nonempty = true, resourceType = RenewVO.class, checkAccount = true)
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
