package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Created by wangwg on 2017/10/09
 */

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = NodeConstant.ACTION_EXT_CATEGORY, names = {"update"}, adminOnly = true)
public class APIUpdateNodeExtensionInfoMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String newNodeExtensionInfo;

    public String getNewNodeExtensionInfo() {
        return newNodeExtensionInfo;
    }

    public void setNewNodeExtensionInfo(String newNodeExtensionInfo) {
        this.newNodeExtensionInfo = newNodeExtensionInfo;
    }
}
