package org.zstack.billing.header.identity.receipt;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
@Action(category = BillingConstant.ACTION_CATEGORY, names = {"receipt"})
public class APIConfirmReceiptMsg  extends APIMessage{

    @APIParam(emptyString = false)
    private String receiptUuid;

    public String getReceiptUuid() {
        return receiptUuid;
    }

    public void setReceiptUuid(String receiptUuid) {
        this.receiptUuid = receiptUuid;
    }
}
