package com.syscxp.tunnel.header.host;

import com.syscxp.header.host.APIUpdateHostMsg;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.node.NodeVO;

/**
 * Created by DCY on 2017-08-30
 */
public class APIUpdateMonitorHostMsg extends APIUpdateHostMsg {

    @APIParam(emptyString = false, resourceType = NodeVO.class)
    private String nodeUuid;

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

}
