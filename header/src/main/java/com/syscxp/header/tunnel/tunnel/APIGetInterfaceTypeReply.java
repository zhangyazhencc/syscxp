package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
public class APIGetInterfaceTypeReply extends APIReply {
    private List<String> types;

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
