package org.zstack.billing.header.identity.order;

import org.zstack.billing.header.identity.receipt.ReceiptInfoVO;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Deprecated
public class APIDeleteCanceledOrderMsg extends APIMessage {
    @APIParam(nonempty = true, resourceType = ReceiptInfoVO.class, checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
