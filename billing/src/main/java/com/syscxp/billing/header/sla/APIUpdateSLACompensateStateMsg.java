package com.syscxp.billing.header.sla;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_SLA, names = "update")
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

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
            ntfy("Update SLACompensateVO")
                    .resource(uuid, SLACompensateVO.class)
                    .messageAndEvent(that, evt).done();
            }
        };
    }
}
