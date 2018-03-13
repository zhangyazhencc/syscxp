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
 * @Cretion Date: 2017-09-11.
 * @Description: 创建监控通道.
 */

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = MonitorConstant.ACTION_CATEGORY, names = {"update"})
public class APIStopTunnelMonitorMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class, checkAccount = true, maxLength = 32)
    private String tunnelUuid;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Stop Tunnel Monitor")
                        .resource(tunnelUuid, TunnelVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
