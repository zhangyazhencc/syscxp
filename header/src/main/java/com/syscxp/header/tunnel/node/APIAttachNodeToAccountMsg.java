package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/5/3
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = NodeConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APIAttachNodeToAccountMsg extends APIMessage {

    @APIParam(emptyString = false, maxLength = 32)
    private String accountUuid;

    @APIParam(emptyString = false, maxLength = 32, resourceType = NodeVO.class)
    private String nodeUuid;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APIAttachNodeToAccountEvent) evt).getInventory().getUuid();
                }

                ntfy("Attach Node To Account")
                        .resource(uuid, AccountNodeRefVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
