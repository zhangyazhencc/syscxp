package com.syscxp.billing.header.sla;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_SLA, names = {"delete"}, adminOnly = true)
public class APIDeleteSLACompensateMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = SLACompensateVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Delete SLACompensateVO")
                        .resource(uuid, SLACompensateVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
