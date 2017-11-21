package com.syscxp.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIDeleteMessage;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.vpn.VpnConstant;

import static com.syscxp.header.message.APIDeleteMessage.DeletionMode.Permissive;

@Action(services = {TunnelConstant.ACTION_SERVICE, VpnConstant.ACTION_SERVICE}, category = HostConstant.ACTION_CATEGORY, adminOnly = true)
public class APIDeleteHostMsg extends APIDeleteMessage implements HostMessage {
    /**
     * @desc host uuid
     */
    @APIParam
    private String uuid;

    public APIDeleteHostMsg() {
    }

    public APIDeleteHostMsg(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getHostUuid() {
        return getUuid();
    }

    public static APIDeleteHostMsg __example__() {
        APIDeleteHostMsg msg = new APIDeleteHostMsg();
        msg.setUuid(uuid());
        msg.setDeletionMode(Permissive);
        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Deleted").resource(uuid, HostVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
