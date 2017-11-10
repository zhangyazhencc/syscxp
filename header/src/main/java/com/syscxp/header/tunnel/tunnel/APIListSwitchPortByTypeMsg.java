package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.switchs.SwitchPortType;

/**
 * Created by DCY on 2017-09-11
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
public class APIListSwitchPortByTypeMsg extends APISyncCallMessage {
    @APIParam(emptyString = false, resourceType = EndpointVO.class)
    private String uuid;
    @APIParam
    private SwitchPortType type;

    public SwitchPortType getType() {
        return type;
    }

    public void setType(SwitchPortType type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
