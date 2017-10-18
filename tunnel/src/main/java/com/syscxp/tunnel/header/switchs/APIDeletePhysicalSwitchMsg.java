package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.manage.SwitchConstant;

/**
 * Created by DCY on 2017-09-13
 */
@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"delete"}, adminOnly = true)
public class APIDeletePhysicalSwitchMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = PhysicalSwitchVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
