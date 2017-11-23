package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.node.NodeEO;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "delete")
public class APIDeleteSolutionMsg extends APIMessage {

    @APIParam(maxLength = 32, emptyString = false, resourceType = SolutionVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
