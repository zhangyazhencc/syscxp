package org.zstack.billing.header.receipt;

import org.zstack.header.billing.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
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
