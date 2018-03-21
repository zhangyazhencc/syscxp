package com.syscxp.billing.header.receipt;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"delete"})
public class APIDeleteReceiptInfoMsg  extends APIMessage {

    @APIParam(emptyString = false, resourceType = ReceiptInfoVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Delete ReceiptInfoVO")
                        .resource(uuid, ReceiptInfoVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
