package org.zstack.billing.header.bill;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

@Action(category = BillingConstant.ACTION_CATEGORY_ORDER, names = {"read"})
public class APIGetBillMsg extends APISyncCallMessage{

    @APIParam(emptyString = false, resourceType = BillVO.class,checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
