package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.AliEdgeRouterConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.edgeLine.APICreateEdgeLineEvent;
import com.syscxp.header.tunnel.edgeLine.EdgeLineVO;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = AliEdgeRouterConstant.ACTION_CATEGORY, names = {"create"})
public class APICreateAliEdgeRouterConfigMsg extends APIMessage {
    @APIParam(maxLength = 64)
    private String aliRegionId;
    @APIParam(maxLength = 64)
    private String aliRegionName;
    @APIParam(maxLength = 32)
    private String physicalLineUuid;
    @APIParam(maxLength = 32)
    private String switchPortUuid;

    public String getAliRegionId() {
        return aliRegionId;
    }

    public void setAliRegionId(String aliRegionId) {
        this.aliRegionId = aliRegionId;
    }

    public String getAliRegionName() {
        return aliRegionName;
    }

    public void setAliRegionName(String aliRegionName) {
        this.aliRegionName = aliRegionName;
    }

    public String getPhysicalLineUuid() {
        return physicalLineUuid;
    }

    public void setPhysicalLineUuid(String physicalLineUuid) {
        this.physicalLineUuid = physicalLineUuid;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateAliEdgeRouterConfigEvent) evt).getInventory().getUuid();
                }

                ntfy("Create AliEdgeRouterConfigVO")
                        .resource(uuid, AliEdgeRouterConfigVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
