package com.syscxp.billing.header.sla;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY_SLA)
public class APIUpdateSLACompensateStateMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = SLACompensateVO.class)
    private String uuid;

    @APIParam(emptyString = false)
    private SLAState state;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public SLAState getState() {
        return state;
    }

    public void setState(SLAState state) {
        this.state = state;
    }
}
