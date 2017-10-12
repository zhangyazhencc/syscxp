package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

/**
 * Create by DCY on 2017/10/11
 */
public class APICreateInnerVlanMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = TunnelVO.class)
    private String tunnelUuid;
    @APIParam
    private Integer startVlan;
    @APIParam
    private Integer endVlan;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public Integer getStartVlan() {
        return startVlan;
    }

    public void setStartVlan(Integer startVlan) {
        this.startVlan = startVlan;
    }

    public Integer getEndVlan() {
        return endVlan;
    }

    public void setEndVlan(Integer endVlan) {
        this.endVlan = endVlan;
    }
}
