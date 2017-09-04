package org.zstack.billing.header.balance;

import org.zstack.billing.header.order.Category;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APIAllotDischargeMsg extends APIMessage{

    @APIParam(emptyString = false, resourceType = AccountDischargeVO.class)
    private String uuid;

    @APIParam(numberRange = {1, 100})
    private int discharge;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getDischarge() {
        return discharge;
    }

    public void setDischarge(int discharge) {
        this.discharge = discharge;
    }
}
