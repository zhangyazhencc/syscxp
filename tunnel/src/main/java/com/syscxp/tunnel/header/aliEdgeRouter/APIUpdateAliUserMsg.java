package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

public class APIUpdateAliUserMsg extends APIMessage {
    @APIParam(checkAccount = true,resourceType = AliUserVO.class)
    private String uuid;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;
    @APIParam(maxLength = 64)
    private String aliAccountUuid;
    @APIParam(maxLength = 32)
    private String AliAccessKeyID;
    @APIParam(maxLength = 32)
    private String AliAccessKeySecret;

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

    public String getAliAccountUuid() {
        return aliAccountUuid;
    }

    public void setAliAccountUuid(String aliAccountUuid) {
        this.aliAccountUuid = aliAccountUuid;
    }

    public String getAliAccessKeyID() {
        return AliAccessKeyID;
    }

    public void setAliAccessKeyID(String aliAccessKeyID) {
        AliAccessKeyID = aliAccessKeyID;
    }

    public String getAliAccessKeySecret() {
        return AliAccessKeySecret;
    }

    public void setAliAccessKeySecret(String aliAccessKeySecret) {
        AliAccessKeySecret = aliAccessKeySecret;
    }
}
