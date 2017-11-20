package com.syscxp.billing.header.receipt;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"create"})
public class APICreateReceiptPostAddressMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String name;

    @APIParam(emptyString = false)
    private String telephone;

    @APIParam(emptyString = false)
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateReceiptPostAddressEvent) evt).getInventory().getUuid();
                }
                ntfy("Create ReceiptPostAddressVO")
                        .resource(uuid, ReceiptPostAddressVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
