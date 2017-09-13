package org.zstack.tunnel.header.node;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-13.
 * @Description: 删除节点数据.
 */

@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"delete"})
public class APIDeleteNodeMsg extends APIMessage {
    @APIParam(emptyString = false, resourceType = NodeEO.class, checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
