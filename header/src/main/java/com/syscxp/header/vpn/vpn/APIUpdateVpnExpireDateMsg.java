package com.syscxp.header.vpn.vpn;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

import java.sql.Timestamp;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-04-26.
 * @Description: .
 */

@InnerCredentialCheck
public class APIUpdateVpnExpireDateMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,resourceType = TunnelVO.class)
    private String uuid;

    @APIParam
    private Timestamp expireDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }
}
