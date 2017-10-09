package com.syscxp.tunnel.header.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.manage.NodeConstant;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-13.
 * @Description: 删除节点数据.
 */

@Action(category = NodeConstant.ACTION_CATEGORY, names = {"delete"}, adminOnly = true)
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
