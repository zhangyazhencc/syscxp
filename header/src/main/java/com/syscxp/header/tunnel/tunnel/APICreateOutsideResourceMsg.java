package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/3/28
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreateOutsideResourceMsg extends APIMessage {

    @APIParam(emptyString = false, maxLength = 32)
    private String resourceType;

    @APIParam(emptyString = false, maxLength = 32)
    private String resourceUuid;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateOutsideResourceEvent) evt).getInventory().getUuid();
                }

                ntfy("Create OutsideResourceVO")
                        .resource(uuid, OutsideResourceVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
