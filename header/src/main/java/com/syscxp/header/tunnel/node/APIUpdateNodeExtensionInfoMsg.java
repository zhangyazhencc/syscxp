package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.utils.gson.JSONObjectUtil;

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

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                NodeExtensionInfo info = JSONObjectUtil.toObject(((APIUpdateNodeExtensionInfoEvent) evt)
                        .getInventory(),NodeExtensionInfo.class);
                ntfy("Update NodeExtensionInfo")
                        .resource(info.getNode_id(), NodeVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
