package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.AliEdgeRouterConstant;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(services = {"tunnel"}, category = AliEdgeRouterConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateAliEdgeRouterMsg extends APIMessage{
    @APIParam(checkAccount = true,resourceType = AliEdgeRouterVO.class)
    private String uuid;
    @APIParam(required = false,maxLength = 32)
    private String accountUuid;
    @APIParam(required = false,maxLength = 128)
    private String name;
    @APIParam(required = false,maxLength = 255)
    private String description;
    @APIParam(emptyString = false,required = false)
    private String localGatewayIp;
    @APIParam(emptyString = false,required = false)
    private String peerGatewayIp;
    @APIParam(emptyString = false,required = false)
    private String peeringSubnetMask;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String aliAccessKeyID;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String aliAccessKeySecret;

    
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocalGatewayIp() {
        return localGatewayIp;
    }

    public void setLocalGatewayIp(String localGatewayIp) {
        this.localGatewayIp = localGatewayIp;
    }

    public String getPeerGatewayIp() {
        return peerGatewayIp;
    }

    public void setPeerGatewayIp(String peerGatewayIp) {
        this.peerGatewayIp = peerGatewayIp;
    }

    public String getPeeringSubnetMask() {
        return peeringSubnetMask;
    }

    public void setPeeringSubnetMask(String peeringSubnetMask) {
        this.peeringSubnetMask = peeringSubnetMask;
    }

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
}
