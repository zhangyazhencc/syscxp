package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.manage.TunnelConstant;

/**
 * Create by DCY on 2017/10/11
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"delete"})
public class APIDeleteInnerVlanMsg extends APIMessage {
    @APIParam(emptyString = false, resourceType = QinqVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
