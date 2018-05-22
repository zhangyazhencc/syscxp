package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.CloudHubConstant;


@Action(services = {CloudHubConstant.ACTION_SERVICE}, category = CloudHubConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateCloudHubMsg extends APIMessage {

    @APIParam(resourceType = CloudHubVO.class, checkAccount = true)
    private String uuid;

    @APIParam(emptyString = false)
    private String name;

    @APIParam(required = false)
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

                ntfy("Update CloudHub")
                        .resource(uuid, CloudHubVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
