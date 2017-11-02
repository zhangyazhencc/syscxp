package com.syscxp.header.alarm;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.TunnelForAlarmInventory;

import java.util.List;

/**
 * Created by wangwg on 2017/11/02
 */
@Action(category = AlarmConstant.ACTION_CATEGORY_RESOURCE_POLICY, names = {"update"})
@InnerCredentialCheck
public class APIUpdateTunnelInfoForFalconMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String  tunnel_id;

    @APIParam(emptyString = false)
    private String  endpointA_vlan;

    @APIParam(emptyString = false)
    private String  endpointA_ip;

    @APIParam(emptyString = false)
    private String  endpointZ_vlan;

    @APIParam(emptyString = false)
    private String  endpointZ_ip;

    @APIParam(emptyString = false)
    private String  bandwidth;

    public String getTunnel_id() {
        return tunnel_id;
    }

    public void setTunnel_id(String tunnel_id) {
        this.tunnel_id = tunnel_id;
    }

    public String getEndpointA_vlan() {
        return endpointA_vlan;
    }

    public void setEndpointA_vlan(String endpointA_vlan) {
        this.endpointA_vlan = endpointA_vlan;
    }

    public String getEndpointA_ip() {
        return endpointA_ip;
    }

    public void setEndpointA_ip(String endpointA_ip) {
        this.endpointA_ip = endpointA_ip;
    }

    public String getEndpointZ_vlan() {
        return endpointZ_vlan;
    }

    public void setEndpointZ_vlan(String endpointZ_vlan) {
        this.endpointZ_vlan = endpointZ_vlan;
    }

    public String getEndpointZ_ip() {
        return endpointZ_ip;
    }

    public void setEndpointZ_ip(String endpointZ_ip) {
        this.endpointZ_ip = endpointZ_ip;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }
}
