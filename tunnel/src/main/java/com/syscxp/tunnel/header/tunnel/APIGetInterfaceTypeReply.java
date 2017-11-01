package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIReply;
import com.syscxp.tunnel.header.switchs.SwitchPortType;

import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
public class APIGetInterfaceTypeReply extends APIReply {
    private List<SwitchPortType> types;

    public List<SwitchPortType> getTypes() {
        return types;
    }

    public void setTypes(List<SwitchPortType> types) {
        this.types = types;
    }
}
