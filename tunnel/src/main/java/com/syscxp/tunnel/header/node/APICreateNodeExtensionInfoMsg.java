package com.syscxp.tunnel.header.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.manage.NodeConstant;

/**
 * Created by wangwg on 2017/10/09.
 */

@Action(category = NodeConstant.ACTION_CATEGORY, names = {"create"})

public class APICreateNodeExtensionInfoMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 255)
    private String nodeExtensionInfo;

    public String getNodeExtensionInfo() {
        return nodeExtensionInfo;
    }

    public void setNodeExtensionInfo(String nodeExtensionInfo) {
        this.nodeExtensionInfo = nodeExtensionInfo;
    }
}
