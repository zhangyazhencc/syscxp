package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.CloudHubConstant;


@Action(services = {CloudHubConstant.ACTION_SERVICE}, category = CloudHubConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateCloudHubOfferingMsg extends APIMessage {

    @APIParam(resourceType = CloudHubVO.class, checkAccount = true)
    private String uuid;

    @APIParam(resourceType = CloudHubOfferingVO.class)
    private String CloudHubOfferUuid;


    public String getCloudHubOfferUuid() {
        return CloudHubOfferUuid;
    }

    public void setCloudHubOfferUuid(String cloudHubOfferUuid) {
        CloudHubOfferUuid = cloudHubOfferUuid;
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
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APIUpdateCloudHubEvent) evt).getInventory().getUuid();
                }

                ntfy("Update CloudHubOffering")
                        .resource(uuid, CloudHubVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
