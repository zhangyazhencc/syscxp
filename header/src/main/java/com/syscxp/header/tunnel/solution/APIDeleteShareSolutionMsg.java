package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

public class APIDeleteShareSolutionMsg extends APIMessage{
    @APIParam( resourceType = ShareSolutionVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
