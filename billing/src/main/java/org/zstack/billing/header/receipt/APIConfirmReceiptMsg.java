package org.zstack.billing.header.receipt;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APIConfirmReceiptMsg  extends APIMessage{

    @APIParam(emptyString = false)
    private String receiptUuid;

    @APIParam(emptyString = false,required = false)
    private String reason;

    @APIParam(emptyString = false,required = false)
    private ReceiptState state;

    public String getReceiptUuid() {
        return receiptUuid;
    }

    public void setReceiptUuid(String receiptUuid) {
        this.receiptUuid = receiptUuid;
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
