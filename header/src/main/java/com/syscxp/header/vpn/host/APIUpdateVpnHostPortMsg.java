package com.syscxp.header.vpn.host;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.RestRequest;
import org.springframework.http.HttpMethod;


public class APIUpdateVpnHostPortMsg extends APIMessage{
    @APIParam(resourceType = VpnHostVO.class)
    private String uuid;
    @APIParam
    private Integer startPort;
    @APIParam
    private Integer endPort;

    public Integer getStartPort() {
        return startPort;
    }

    public void setStartPort(Integer startPort) {
        this.startPort = startPort;
    }

    public Integer getEndPort() {
        return endPort;
    }

    public void setEndPort(Integer endPort) {
        this.endPort = endPort;
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
                ntfy("Update VpnHostVO")
                        .resource(uuid, VpnHostVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
