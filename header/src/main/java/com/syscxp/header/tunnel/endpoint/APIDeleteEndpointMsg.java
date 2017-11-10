package com.syscxp.header.tunnel.endpoint;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.NodeConstant;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-13.
 * @Description: .
 */
@Action(category = NodeConstant.ACTION_CATEGORY, names = {"delete"}, adminOnly = true)
public class APIDeleteEndpointMsg extends APIMessage {
    @APIParam(emptyString = false, resourceType = EndpointVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}