package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.tunnel.TunnelVO;

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
    private String LocalGatewayIp;
    @APIParam(emptyString = false,required = false)
    private String PeerGatewayIp;
    @APIParam(emptyString = false,required = false)
    private String PeeringSubnetMask;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String AliAccessKeyID;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String AliAccessKeySecret;

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
        return LocalGatewayIp;
    }

    public void setLocalGatewayIp(String localGatewayIp) {
        LocalGatewayIp = localGatewayIp;
    }

    public String getPeerGatewayIp() {
        return PeerGatewayIp;
    }

    public void setPeerGatewayIp(String peerGatewayIp) {
        PeerGatewayIp = peerGatewayIp;
    }

    public String getPeeringSubnetMask() {
        return PeeringSubnetMask;
    }

    public void setPeeringSubnetMask(String peeringSubnetMask) {
        PeeringSubnetMask = peeringSubnetMask;
    }
}
