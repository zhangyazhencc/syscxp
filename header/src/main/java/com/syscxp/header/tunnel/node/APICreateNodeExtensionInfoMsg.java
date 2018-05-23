package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.utils.gson.JSONObjectUtil;

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

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    NodeExtensionInfo info = JSONObjectUtil.toObject(((APICreateNodeExtensionInfoEvent) evt)
                            .getInventory(),NodeExtensionInfo.class);
                    uuid = info.getNode_id();
                }
                ntfy("Create NodeExtensionInfo")
                        .resource(uuid, NodeVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }


}
