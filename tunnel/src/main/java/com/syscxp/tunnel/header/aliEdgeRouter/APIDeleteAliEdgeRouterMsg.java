package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.tunnel.TunnelVO;

public class APIDeleteAliEdgeRouterMsg extends APIMessage {
    @APIParam(checkAccount = true, resourceType = TunnelVO.class)
    private String uuid;

    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;

    @APIParam(required = false)
    private Boolean Flag;

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

    public Boolean getFlag() {
        return Flag;
    }

    public void setFlag(Boolean flag) {
        Flag = flag;
    }
}
