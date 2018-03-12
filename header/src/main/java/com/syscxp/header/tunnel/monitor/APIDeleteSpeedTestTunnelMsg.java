package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: 删除测速专线.
 */

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = MonitorConstant.ACTION_CATEGORY, names = {"delete"}, adminOnly = true)
public class APIDeleteSpeedTestTunnelMsg extends APIMessage {
    @APIParam(emptyString = false, resourceType = SpeedTestTunnelVO.class)
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
                ntfy("Delete SpeedTestTunnelVO")
                        .resource(uuid, SpeedTestTunnelVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
