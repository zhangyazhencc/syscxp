package org.zstack.billing.header.receipt;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"update"})
public class APIUpdateReceiptMsg extends APIMessage{

    @APIParam(emptyString = false)
    private String uuid;

    @APIParam(emptyString = false,required = false)
    private String reason;

    @APIParam(emptyString = false,required = false)
    private ReceiptState state;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ReceiptState getState() {
        return state;
    }

    public void setState(ReceiptState state) {
        this.state = state;
    }
}
