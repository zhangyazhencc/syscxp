package org.zstack.billing.header.balance;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"recharge"}, proxyOnly = true)
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
