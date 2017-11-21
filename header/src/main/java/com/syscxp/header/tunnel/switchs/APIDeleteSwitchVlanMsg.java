package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.SwitchConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Created by DCY on 2017-09-13
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SwitchConstant.ACTION_CATEGORY, names = {"delete"}, adminOnly = true)
public class APIDeleteSwitchVlanMsg extends APIMessage {
    @APIParam(emptyString = false, resourceType = SwitchVlanVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
