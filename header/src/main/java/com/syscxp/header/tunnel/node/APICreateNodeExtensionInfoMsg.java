package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Created by wangwg on 2017/10/09.
 */

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = NodeConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreateNodeExtensionInfoMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String nodeExtensionInfo;

    public String getNodeExtensionInfo() {
        return nodeExtensionInfo;
    }

    public void setNodeExtensionInfo(String nodeExtensionInfo) {
        this.nodeExtensionInfo = nodeExtensionInfo;
    }
}
