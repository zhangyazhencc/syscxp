package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.network.L3EndpointVO;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: 开启三层网络监控.
 */
@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIConfigL3NetworkMonitorEvent.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = MonitorConstant.ACTION_CATEGORY, names = {"update"})
public class APIConfigL3NetworkMonitorMsg extends APIMessage {

    @APIParam(emptyString = false, resourceType = L3EndpointVO.class, maxLength = 32)
    private String l3EndPointUuid;

    @APIParam(required = false, maxLength = 64)
    private String monitorIp;

    private String[] dstL3EndPointUuids;

    public String getL3EndPointUuid() {
        return l3EndPointUuid;
    }

    public void setL3EndPointUuid(String l3EndPointUuid) {
        this.l3EndPointUuid = l3EndPointUuid;
    }

    public String getMonitorIp() {
        return monitorIp;
    }

    public void setMonitorIp(String monitorIp) {
        this.monitorIp = monitorIp;
    }

    public String[] getDstL3EndPointUuids() {
        return dstL3EndPointUuids;
    }

    public void setDstL3EndPointUuids(String[] dstL3EndPointUuids) {
        this.dstL3EndPointUuids = dstL3EndPointUuids;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Start L3Network Monitor")
                        .resource(l3EndPointUuid, L3EndpointVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
