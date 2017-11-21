package com.syscxp.billing.header.receipt;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"update"})
public class APIUpdateReceiptMsg extends APIMessage{

    @APIParam(emptyString = false)
    private String uuid;

    @APIParam(emptyString = false,required = false)
    private String reason;

    @APIParam(emptyString = false,required = false)
    private String receiptNO;

    @APIParam(emptyString = false,required = false)
    private String opMan;

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

    public String getReceiptNO() {
        return receiptNO;
    }

    public void setReceiptNO(String receiptNO) {
        this.receiptNO = receiptNO;
    }

    public String getOpMan() {
        return opMan;
    }

    public void setOpMan(String opMan) {
        this.opMan = opMan;
    }
}
