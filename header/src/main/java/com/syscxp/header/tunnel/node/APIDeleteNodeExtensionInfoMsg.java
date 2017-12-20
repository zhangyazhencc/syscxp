package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIDeleteMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Created by wangwg on 2017/10/09
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = NodeConstant.ACTION_CATEGORY, names = {"delete"}, adminOnly = true)
public class APIDeleteNodeExtensionInfoMsg extends APIDeleteMessage {

    @APIParam(emptyString = false,resourceType = NodeVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
