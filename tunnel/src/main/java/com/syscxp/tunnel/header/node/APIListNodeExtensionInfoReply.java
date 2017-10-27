package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APIReply;

/**
 * Created by wangwg on 2017/10/26
 */
public class APIListNodeExtensionInfoReply extends APIReply {

    private NodeExtensionInfoList nodeExtensionInfoList;

    public NodeExtensionInfoList getNodeExtensionInfoList() {
        return nodeExtensionInfoList;
    }

    public void setNodeExtensionInfoList(NodeExtensionInfoList nodeExtensionInfoList) {
        this.nodeExtensionInfoList = nodeExtensionInfoList;
    }
}


