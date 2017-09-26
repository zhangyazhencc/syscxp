package org.zstack.vpn.header.host;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
import org.zstack.vpn.vpn.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"create"}, adminOnly = true)
public class APICreateHostInterfaceMsg extends APIMessage {
    @APIParam(emptyString = false)
    private String name;
    @APIParam(required = false)
    private String hostUuid;
    @APIParam(emptyString = false)
    private String endpointUuid;
    @APIParam(emptyString = false)
    private String interfaceUuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
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
                        .resource(uuid, HostInterfaceVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
