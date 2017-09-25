package org.zstack.billing.header.sla;

import org.zstack.header.billing.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

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
