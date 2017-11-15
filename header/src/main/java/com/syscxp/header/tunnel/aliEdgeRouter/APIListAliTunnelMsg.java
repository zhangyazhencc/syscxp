package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
public class APIListAliTunnelMsg extends APISyncCallMessage {
    @APIParam(required = false, maxLength = 32)
    private String accountUuid;
    @APIParam(maxLength = 128)
    private String aliRegionName;

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

    public String getAliRegionName() {
        return aliRegionName;
    }

    public void setAliRegionName(String aliRegionName) {
        this.aliRegionName = aliRegionName;
    }
}
