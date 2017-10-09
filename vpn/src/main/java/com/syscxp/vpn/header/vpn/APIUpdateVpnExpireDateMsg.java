package com.syscxp.vpn.header.vpn;

import com.syscxp.header.billing.OrderType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.vpn.vpn.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"update"}, adminOnly = true)
public class APIUpdateVpnExpireDateMsg extends APIVpnMessage {
    @APIParam(resourceType = VpnVO.class, checkAccount = true)
    private String uuid;
    @APIParam
    private Integer duration;
    @APIParam
    private OrderType type;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
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
                ntfy("Update ExpireDate")
                        .resource(uuid, VpnVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
