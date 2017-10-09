package com.syscxp.tunnel.header.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.tunnel.manage.NodeConstant;

/**
 * Created by wangwg on 2017/10/09
 */
@Action(category = NodeConstant.ACTION_CATEGORY, names = {"read"})
public class APIGetNodeExtensionInfoMsg extends APISyncCallMessage {

    @APIParam(emptyString = false)
    private String nodeId;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
