package com.syscxp.billing.header.sla;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY_SLA, names = {"delete"})

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
