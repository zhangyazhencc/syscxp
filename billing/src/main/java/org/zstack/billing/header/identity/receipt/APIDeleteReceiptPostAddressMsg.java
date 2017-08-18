package org.zstack.billing.header.identity.receipt;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APIDeleteReceiptPostAddressMsg extends APIMessage{

    @APIParam(nonempty = true,resourceType = ReceiptPostAddressVO.class, checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
