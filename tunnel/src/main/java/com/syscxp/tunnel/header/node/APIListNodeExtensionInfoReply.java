package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APIReply;

/**
 * Created by wangwg on 2017/10/26
 */
public class APIListNodeExtensionInfoReply extends APIReply {

    private String nodeExtensionInfoList;

    public String getNodeExtensionInfoList() {
        return nodeExtensionInfoList;
    }

    public void setNodeExtensionInfoList(String nodeExtensionInfoList) {
        this.nodeExtensionInfoList = nodeExtensionInfoList;
    }
}


