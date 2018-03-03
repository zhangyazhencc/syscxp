package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = NodeConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APIAttachNodeToZoneMsg extends APIMessage{

    @APIParam(emptyString = false, maxLength = 32, resourceType = NodeVO.class)
    private String nodeUuid;

    @APIParam(emptyString = false, maxLength = 32, resourceType = ZoneVO.class)
    private String zoneUuid;

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public String getZoneUuid() {
        return zoneUuid;
    }

    public void setZoneUuid(String zoneUuid) {
        this.zoneUuid = zoneUuid;
    }
}
