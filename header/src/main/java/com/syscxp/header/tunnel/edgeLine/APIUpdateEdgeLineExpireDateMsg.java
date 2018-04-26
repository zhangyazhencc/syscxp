package com.syscxp.header.tunnel.edgeLine;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.sql.Timestamp;

/**
 * Create by DCY on 2018/4/26
 */
@InnerCredentialCheck
public class APIUpdateEdgeLineExpireDateMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,resourceType = EdgeLineVO.class)
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
