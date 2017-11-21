package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = NodeConstant.ACTION_CATEGORY, names = {"read"})
public class APIListProvinceNodeMsg extends APISyncCallMessage {
    @APIParam(required = false)
    private String country;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
