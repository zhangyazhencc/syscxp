package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.host.APICreateHostSwitchMonitorEvent;
import com.syscxp.header.tunnel.host.HostSwitchMonitorVO;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: 创建测速专线.
 */

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = MonitorConstant.ACTION_CATEGORY, names = {"create"},  adminOnly = true)
public class APICreateSpeedTestTunnelMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class)
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
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateSpeedTestTunnelEvent) evt).getInventory().getUuid();
                }
                ntfy("Create SpeedTestTunnelVO")
                        .resource(uuid, SpeedTestTunnelVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
