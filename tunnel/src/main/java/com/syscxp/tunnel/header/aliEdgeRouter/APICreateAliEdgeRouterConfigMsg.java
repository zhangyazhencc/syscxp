package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

public class APICreateAliEdgeRouterConfigMsg extends APIMessage {
    @APIParam(maxLength = 64)
    private String aliRegionId;
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
