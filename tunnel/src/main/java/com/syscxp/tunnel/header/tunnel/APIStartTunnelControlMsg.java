package com.syscxp.tunnel.header.tunnel;


import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/10/24
 */
@Action(category = TunnelConstant.ACTION_CATEGORY)
public class APIStartTunnelControlMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = TunnelVO.class)
    private String tunnelUuid;

    @APIParam(emptyString = false,validValues = {"Enabled","Disabled","ModifyBandwidth","ModifyPorts","Create","Delete"})
    private TunnelAction tunnelAction;

    @APIParam(required = false)
    private Long updateBandwidth;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public TunnelAction getTunnelAction() {
        return tunnelAction;
    }

    public void setTunnelAction(TunnelAction tunnelAction) {
        this.tunnelAction = tunnelAction;
    }

    public Long getUpdateBandwidth() {
        return updateBandwidth;
    }

    public void setUpdateBandwidth(Long updateBandwidth) {
        this.updateBandwidth = updateBandwidth;
    }
}
