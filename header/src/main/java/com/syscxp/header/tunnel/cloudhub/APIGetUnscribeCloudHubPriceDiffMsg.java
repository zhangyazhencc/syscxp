package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.CloudHubConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/5/28
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = CloudHubConstant.ACTION_CATEGORY, names = {"read"})
public class APIGetUnscribeCloudHubPriceDiffMsg extends APISyncCallMessage {
    @APIParam(emptyString = false,resourceType = CloudHubVO.class, checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
