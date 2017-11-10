package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

/**
 * Created by wangwg on 2017/10/09
 */
//@Action(category = NodeConstant.ACTION_CATEGORY, names = {"update"})
@SuppressCredentialCheck
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
