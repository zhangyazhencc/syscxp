package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/11/9
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, adminOnly = true)
public class APIReCallControllerMsg extends APIMessage {
    @APIParam(emptyString = false,maxLength = 32,resourceType = TaskResourceVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
