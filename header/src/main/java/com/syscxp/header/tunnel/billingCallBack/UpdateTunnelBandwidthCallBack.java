package com.syscxp.header.tunnel.billingCallBack;

import com.syscxp.header.billing.NotifyCallBackData;

/**
 * Create by DCY on 2017/11/16
 */
public class UpdateTunnelBandwidthCallBack extends NotifyCallBackData {

    private Long bandwidth;

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}