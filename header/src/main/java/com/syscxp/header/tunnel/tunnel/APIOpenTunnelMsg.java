package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/10/31
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIOpenTunnelMsg extends APIMessage implements TunnelMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class, checkAccount = true)
    private String uuid;
    @APIParam
    private boolean saveOnly;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isSaveOnly() {
        return saveOnly;
    }

    public void setSaveOnly(boolean saveOnly) {
        this.saveOnly = saveOnly;
    }

    @Override
    public String getTunnelUuid(){
        return uuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Open Tunnel")
                        .resource(uuid, TunnelVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
