package com.syscxp.header.alarm;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;

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
    private Integer  endpointA_vid;

    @APIParam(emptyString = false)
    private String  endpointA_ip;

    @APIParam(emptyString = false)
    private Integer  endpointB_vid;

    @APIParam(emptyString = false)
    private String  endpointB_ip;

    @APIParam(emptyString = false)
    private Long  bandwidth;

    public String getTunnel_id() {
        return tunnel_id;
    }

    public void setTunnel_id(String tunnel_id) {
        this.tunnel_id = tunnel_id;
    }

    public Integer getEndpointA_vid() {
        return endpointA_vid;
    }

    public void setEndpointA_vid(Integer endpointA_vid) {
        this.endpointA_vid = endpointA_vid;
    }

    public String getEndpointA_ip() {
        return endpointA_ip;
    }

    public void setEndpointA_ip(String endpointA_ip) {
        this.endpointA_ip = endpointA_ip;
    }

    public Integer getEndpointB_vid() {
        return endpointB_vid;
    }

    public void setEndpointB_vid(Integer endpointB_vid) {
        this.endpointB_vid = endpointB_vid;
    }

    public String getEndpointB_ip() {
        return endpointB_ip;
    }

    public void setEndpointB_ip(String endpointB_ip) {
        this.endpointB_ip = endpointB_ip;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
