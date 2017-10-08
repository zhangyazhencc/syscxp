package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

/**
 * Created by DCY on 2017-09-17
 */
public class APIDeleteTunnelMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class)
    private String uuid;

    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        if(getSession().getType() == AccountType.SystemAdmin){
            return accountUuid;
        }else{
            return getSession().getAccountUuid();
        }
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
