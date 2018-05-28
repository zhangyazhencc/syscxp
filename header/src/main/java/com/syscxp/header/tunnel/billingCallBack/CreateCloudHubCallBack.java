package com.syscxp.header.tunnel.billingCallBack;

import com.syscxp.header.billing.NotifyCallBackData;
import com.syscxp.header.tunnel.cloudhub.CloudHubVO;

/**
 * Create by DCY on 2018/5/24
 */
public class CreateCloudHubCallBack extends NotifyCallBackData {

    private CloudHubVO cloudHubVO;

    public CloudHubVO getCloudHubVO() {
        return cloudHubVO;
    }

    public void setCloudHubVO(CloudHubVO cloudHubVO) {
        this.cloudHubVO = cloudHubVO;
    }
}
