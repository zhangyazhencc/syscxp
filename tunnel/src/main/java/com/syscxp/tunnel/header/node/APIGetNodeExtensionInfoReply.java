package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APIReply;

/**
 * Created by wangwg on 2017/10/09
 */
public class APIGetNodeExtensionInfoReply extends APIReply {

    private String nodeExtensionInfo;

    public String getNodeExtensionInfo() {
        return nodeExtensionInfo;
    }

    public void setNodeExtensionInfo(String nodeExtensionInfo) {
        this.nodeExtensionInfo = nodeExtensionInfo;
    }
}


