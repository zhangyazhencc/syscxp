package com.syscxp.header.alarm;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

/**
 * Created by wangwg on 2017/11/02
 */
@InnerCredentialCheck
public class APIUpdateTunnelInfoForFalconMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String tunnelUuid;

    @APIParam(emptyString = false)
    private Integer switchA_vlan;

    @APIParam(emptyString = false)
    private String switchA_ip;

    @APIParam(emptyString = false)
    private Integer switchB_vlan;

    @APIParam(emptyString = false)
    private String switchB_ip;

    @APIParam(emptyString = false)
    private Long bandwidth;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public Integer getSwitchA_vlan() {
        return switchA_vlan;
    }

    public void setSwitchA_vlan(Integer switchA_vlan) {
        this.switchA_vlan = switchA_vlan;
    }

    public String getSwitchA_ip() {
        return switchA_ip;
    }

    public void setSwitchA_ip(String switchA_ip) {
        this.switchA_ip = switchA_ip;
    }

    public Integer getSwitchB_vlan() {
        return switchB_vlan;
    }

    public void setSwitchB_vlan(Integer switchB_vlan) {
        this.switchB_vlan = switchB_vlan;
    }

    public String getSwitchB_ip() {
        return switchB_ip;
    }

    public void setSwitchB_ip(String switchB_ip) {
        this.switchB_ip = switchB_ip;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
