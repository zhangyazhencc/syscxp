package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Created by DCY on 2017-09-14
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"delete"})
public class APIDeleteInterfaceMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
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
