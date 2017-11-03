package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIReply;

/**
 * Create by DCY on 2017/11/3
 */
public class APIGetVlanAutoReply extends APIReply {

    private Integer vlan;

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }
}
