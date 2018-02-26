package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIReply;

/**
 * Create by DCY on 2018/2/24
 */
public class APIGetSwitchPortDetailReply extends APIReply {

    private Long bandwidthPaid;         //已售带宽

    private Long bandwidthUsed;         //已使用带宽

    private Double bandwidthUsage;      //带宽使用率

    private Integer tunnelNumEnabled;   //已开通专线数量

    public Long getBandwidthPaid() {
        return bandwidthPaid;
    }

    public void setBandwidthPaid(Long bandwidthPaid) {
        this.bandwidthPaid = bandwidthPaid;
    }

    public Long getBandwidthUsed() {
        return bandwidthUsed;
    }

    public void setBandwidthUsed(Long bandwidthUsed) {
        this.bandwidthUsed = bandwidthUsed;
    }

    public Double getBandwidthUsage() {
        return bandwidthUsage;
    }

    public void setBandwidthUsage(Double bandwidthUsage) {
        this.bandwidthUsage = bandwidthUsage;
    }

    public Integer getTunnelNumEnabled() {
        return tunnelNumEnabled;
    }

    public void setTunnelNumEnabled(Integer tunnelNumEnabled) {
        this.tunnelNumEnabled = tunnelNumEnabled;
    }
}
