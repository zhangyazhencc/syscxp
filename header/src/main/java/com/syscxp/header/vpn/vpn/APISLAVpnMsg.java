package com.syscxp.header.vpn.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.vpn.VpnConstant;

@InnerCredentialCheck
@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"update"})
public class APISLAVpnMsg extends APISyncCallMessage {

    @APIParam(emptyString = false, resourceType = VpnVO.class)
    private String uuid;

    @APIParam
    private Integer duration;

    @APIParam
    private String slaUuid;

    public String getSlaUuid() {
        return slaUuid;
    }

    public void setSlaUuid(String slaUuid) {
        this.slaUuid = slaUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
                ntfy("VpnVO SLA")
                        .resource(uuid, VpnVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
