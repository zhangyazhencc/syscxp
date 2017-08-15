package org.zstack.billing.header.identity.receipt;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APIDeleteReceiptInfoMsg  extends APIMessage {

    @APIParam(nonempty = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
