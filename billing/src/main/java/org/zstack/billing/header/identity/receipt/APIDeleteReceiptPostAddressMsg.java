package org.zstack.billing.header.identity.receipt;

import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

public class APIDeleteReceiptPostAddressMsg extends APIMessage {

    @APIParam(nonempty = true,resourceType = ReceiptPostAddressVO.class, checkAccount = true)
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
