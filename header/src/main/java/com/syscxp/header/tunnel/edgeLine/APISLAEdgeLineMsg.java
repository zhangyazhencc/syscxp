package com.syscxp.header.tunnel.edgeLine;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.EdgeLineConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/1/12
 */
@InnerCredentialCheck
@Action(services = {EdgeLineConstant.ACTION_SERVICE}, category = EdgeLineConstant.ACTION_CATEGORY, names = {"update"})
public class APISLAEdgeLineMsg extends APISyncCallMessage {

    @APIParam(emptyString = false, resourceType = EdgeLineVO.class)
    private String uuid;
    @APIParam
    private String slaUuid;
    @APIParam
    private Integer duration;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSlaUuid() {
        return slaUuid;
    }

    public void setSlaUuid(String slaUuid) {
        this.slaUuid = slaUuid;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("EdgeLineVO SLA")
                        .resource(uuid, EdgeLineVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
