package com.syscxp.billing.header.receipt;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
@Action(category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"delete"})
public class APIDeleteReceiptPostAddressMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = ReceiptPostAddressVO.class, checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Delete ReceiptPostAddressVO")
                        .resource(uuid, ReceiptPostAddressVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
