package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.CloudHubConstant;
import com.syscxp.header.tunnel.TunnelConstant;


@Action(services = {TunnelConstant.ACTION_SERVICE}, category = CloudHubConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateCloudHubOfferingMsg extends APIMessage {

    @APIParam(resourceType = CloudHubVO.class, checkAccount = true)
    private String uuid;

    @APIParam(resourceType = CloudHubOfferingVO.class)
    private String cloudHubOfferUuid;

    public String getCloudHubOfferUuid() {
        return cloudHubOfferUuid;
    }

    public void setCloudHubOfferUuid(String cloudHubOfferUuid) {
        this.cloudHubOfferUuid = cloudHubOfferUuid;
    }

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
                ntfy("Update CloudHub Offering")
                        .resource(uuid, CloudHubVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
