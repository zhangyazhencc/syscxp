package org.zstack.billing.header.identity.receipt;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
@Action(category = BillingConstant.ACTION_CATEGORY, names = {"receipt"})
public class APIDeleteReceiptInfoMsg  extends APIMessage {

    @APIParam(nonempty = true, resourceType = ReceiptInfoVO.class, checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
