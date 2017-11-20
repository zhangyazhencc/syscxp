package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-17.
 * @Description: falcon-alarm获取端点数据.
 */
public class APIGetEndpointTunnelsMsg extends APISyncCallMessage {
    @APIParam(emptyString = false,maxLength = 64)
    private String physicalSwitchMip;

    @APIParam(emptyString = false)
    private Integer vlan;

    public String getPhysicalSwitchMip() {
        return physicalSwitchMip;
    }

    public void setPhysicalSwitchMip(String physicalSwitchMip) {
        this.physicalSwitchMip = physicalSwitchMip;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }
}
