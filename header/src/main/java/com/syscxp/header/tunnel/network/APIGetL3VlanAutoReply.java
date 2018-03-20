package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIReply;

/**
 * Create by DCY on 2018/3/20
 */
public class APIGetL3VlanAutoReply extends APIReply {

    private Integer vlan;

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }
}
