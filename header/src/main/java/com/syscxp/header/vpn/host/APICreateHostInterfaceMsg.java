package com.syscxp.header.vpn.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.vpn.VpnConstant;

public class APICreateHostInterfaceMsg extends APIMessage {
    @APIParam(emptyString = false)
    private String interfaceName;
    @APIParam(required = false)
    private String hostUuid;
    @APIParam(emptyString = false)
    private String endpointUuid;

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateHostInterfaceEvent) evt).getInventory().getUuid();
                }

                ntfy("Create HostInterfaceVO")
                        .resource(uuid, HostInterfaceVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}