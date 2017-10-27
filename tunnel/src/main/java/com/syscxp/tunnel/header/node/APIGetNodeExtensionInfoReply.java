package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APIReply;

/**
 * Created by wangwg on 2017/10/09
 */
public class APIGetNodeExtensionInfoReply extends APIReply {

    private NodeExtensionInfo nodeExtensionInfo;

    public NodeExtensionInfo getNodeExtensionInfo() {
        return nodeExtensionInfo;
    }

    public void setNodeExtensionInfo(NodeExtensionInfo nodeExtensionInfo) {
        this.nodeExtensionInfo = nodeExtensionInfo;
    }
}


