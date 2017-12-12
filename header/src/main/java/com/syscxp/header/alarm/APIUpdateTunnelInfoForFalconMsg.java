package com.syscxp.header.alarm;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

/**
 * Created by wangwg on 2017/11/02
 */
@InnerCredentialCheck
//@SuppressCredentialCheck
public class APIUpdateTunnelInfoForFalconMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String tunnelUuid;

    @APIParam(emptyString = false)
    private Integer switchAVlan;

    @APIParam(emptyString = false)
    private String switchAIp;

    @APIParam(emptyString = false)
    private Integer switchBVlan;

    @APIParam(emptyString = false)
    private String switchBIp;

    @APIParam(emptyString = false)
    private Long bandwidth;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public Integer getSwitchAVlan() {
        return switchAVlan;
    }

    public void setSwitchAVlan(Integer switchAVlan) {
        this.switchAVlan = switchAVlan;
    }

    public String getSwitchAIp() {
        return switchAIp;
    }

    public void setSwitchAIp(String switchAIp) {
        this.switchAIp = switchAIp;
    }

    public Integer getSwitchBVlan() {
        return switchBVlan;
    }

    public void setSwitchBVlan(Integer switchBVlan) {
        this.switchBVlan = switchBVlan;
    }

    public String getSwitchBIp() {
        return switchBIp;
    }

    public void setSwitchBIp(String switchBIp) {
        this.switchBIp = switchBIp;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
