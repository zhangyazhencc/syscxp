package org.zstack.billing.header.bill;

import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

public class APIGetBillMsg extends APISyncCallMessage{

    @APIParam(nonempty = false, resourceType = BillVO.class,checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
