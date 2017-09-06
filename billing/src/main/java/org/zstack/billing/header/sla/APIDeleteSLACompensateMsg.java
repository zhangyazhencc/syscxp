package org.zstack.billing.header.sla;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APIDeleteSLACompensateMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = SLACompensateVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
