package com.syscxp.billing.header.receipt;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"create"})
public class APICreateReceiptInfoMsg extends APIMessage {

    @APIParam(emptyString = false)
    private ReceiptType type;

    @APIParam(emptyString = false)
    private String title;

    @APIParam(required = false)
    private String bankName;

    @APIParam(required = false)
    private String bankAccountNumber;

    @APIParam(required = false)
    private String telephone;

    @APIParam(required = false)
    private String identifyNumber;

    @APIParam(required = false)
    private String address;

    @APIParam(required = false)
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ReceiptType getType() {
        return type;
    }

    public void setType(ReceiptType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getIdentifyNumber() {
        return identifyNumber;
    }

    public void setIdentifyNumber(String identifyNumber) {
        this.identifyNumber = identifyNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateReceiptInfoEvent) evt).getInventory().getUuid();
                }
                ntfy("Create ReceiptInfoVO")
                        .resource(uuid, ReceiptInfoVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
