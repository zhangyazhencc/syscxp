package org.zstack.billing.header.receipt;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APIDeleteReceiptInfoMsg  extends APIMessage {

    @APIParam(nonempty = true, resourceType = ReceiptInfoVO.class, checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
