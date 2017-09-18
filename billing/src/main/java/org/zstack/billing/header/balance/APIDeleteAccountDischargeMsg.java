package org.zstack.billing.header.balance;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APIDeleteAccountDischargeMsg extends APIMessage{

    @APIParam(emptyString = false,resourceType = AccountDischargeVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
