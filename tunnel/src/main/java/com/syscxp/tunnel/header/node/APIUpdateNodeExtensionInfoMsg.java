package com.syscxp.tunnel.header.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.manage.NodeConstant;

/**
 * Created by wangwg on 2017/10/09
 */
@Action(category = NodeConstant.ACTION_CATEGORY, names = {"update"})

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
