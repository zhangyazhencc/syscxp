package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-05.
 * @Description: 获取专线OpenTSDB查询条件.
 */

@SuppressCredentialCheck
public class APIQueryOpentsdbConditionMsg extends APISyncCallMessage {
    @APIParam(emptyString = false, resourceType = TunnelVO.class)
    private String tunnelUuid;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }
}
