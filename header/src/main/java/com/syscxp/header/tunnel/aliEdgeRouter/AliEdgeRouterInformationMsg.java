package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.AliEdgeRouterConstant;

@Action(services = {"tunnel"}, category = AliEdgeRouterConstant.ACTION_CATEGORY)
public class AliEdgeRouterInformationMsg extends APISyncCallMessage{
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String aliAccessKeyID;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String aliAccessKeySecret;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;
    @APIParam(maxLength = 64)
    private String aliAccountUuid;
    @APIParam(maxLength = 64)
    private String aliRegionId;
    @APIParam(maxLength = 64)
    private String vbrUuid;

    public String getAliAccessKeyID() {
        return aliAccessKeyID;
    }

    public void setAliAccessKeyID(String aliAccessKeyID) {
        this.aliAccessKeyID = aliAccessKeyID;
    }

    public String getAliAccessKeySecret() {
        return aliAccessKeySecret;
    }

    public void setAliAccessKeySecret(String aliAccessKeySecret) {
        this.aliAccessKeySecret = aliAccessKeySecret;
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

    public String getAliRegionId() {
        return aliRegionId;
    }

    public void setAliRegionId(String aliRegionId) {
        this.aliRegionId = aliRegionId;
    }

    public String getVbrUuid() {
        return vbrUuid;
    }

    public void setVbrUuid(String vbrUuid) {
        this.vbrUuid = vbrUuid;
    }
}
