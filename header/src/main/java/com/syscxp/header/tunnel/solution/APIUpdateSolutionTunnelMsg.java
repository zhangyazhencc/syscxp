package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = SolutionConstant.ACTION_CATEGORY, names = "update")
public class APIUpdateSolutionTunnelMsg extends  APIMessage {

    @APIParam(maxLength = 32)
    private String uuid;

    @APIParam(maxLength = 128)
    private Long bandwidth;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
