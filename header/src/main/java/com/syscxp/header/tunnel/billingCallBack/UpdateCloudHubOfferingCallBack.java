package com.syscxp.header.tunnel.billingCallBack;

import com.syscxp.header.billing.NotifyCallBackData;

/**
 * Create by DCY on 2018/5/24
 */
public class UpdateCloudHubOfferingCallBack extends NotifyCallBackData {

    private String cloudHubOfferingUuid;

    public String getCloudHubOfferingUuid() {
        return cloudHubOfferingUuid;
    }

    public void setCloudHubOfferingUuid(String cloudHubOfferingUuid) {
        this.cloudHubOfferingUuid = cloudHubOfferingUuid;
    }
}
