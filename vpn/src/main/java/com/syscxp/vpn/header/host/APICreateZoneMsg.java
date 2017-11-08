package com.syscxp.vpn.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.vpn.vpn.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"create"}, adminOnly = true)
public class APICreateZoneMsg extends APIMessage {
    @APIParam(emptyString = false)
    private String name;
    @APIParam(required = false)
    private String description;
    @APIParam(emptyString = false)
    private String province;
    @APIParam(emptyString = false)
    private String nodeUuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateZoneEvent) evt).getInventory().getUuid();
                }

                ntfy("Create ZoneVO")
                        .resource(uuid, ZoneVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
