package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.tunnel.TunnelVO;
import org.springframework.http.HttpMethod;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 创建监控通道.
 */
@RestRequest(
        path = "tunnel",
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIStartTunnelMonitorEvent.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = MonitorConstant.ACTION_CATEGORY, names = {"update"})
public class APIStartTunnelMonitorMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = TunnelVO.class, checkAccount = true, maxLength = 32)
    private String tunnelUuid;

    @APIParam(required = true,maxLength = 64)
    private String monitorCidr;

    @APIParam(required = false,maxLength = 1024)
    private String msg;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Start Tunnel Monitor")
                        .resource(tunnelUuid, TunnelVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
