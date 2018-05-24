package com.syscxp.header.tunnel.billingCallBack;

import com.syscxp.header.billing.NotifyCallBackData;

/**
 * Create by DCY on 2017/11/16
 */
public class UnsubcribeTunnelCallBack extends NotifyCallBackData {

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
