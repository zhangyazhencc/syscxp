package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateAliEdgeRouterConfigMsg extends APIMessage {
    @APIParam(checkAccount = true,resourceType = AliEdgeRouterConfigVO.class)
    private String uuid;
    @APIParam(maxLength = 32)
    private String aliRegionId;
    @APIParam(maxLength = 32)
    private String physicalLineUuid;
    @APIParam(maxLength = 32)
    private String switchPortUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAliRegionId() {
        return aliRegionId;
    }

    public void setAliRegionId(String aliRegionId) {
        this.aliRegionId = aliRegionId;
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
}
