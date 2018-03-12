package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.AliEdgeRouterConstant;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = AliEdgeRouterConstant.ACTION_CATEGORY, names = {"delete"})
public class APIDeleteAliEdgeRouterMsg extends APIMessage {
    @APIParam(emptyString = false, checkAccount = true, resourceType = AliEdgeRouterVO.class)
    private String uuid;

    @APIParam(required = false)
    private Boolean haveConnectIpFlag;

    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String aliAccessKeyID;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String aliAccessKeySecret;

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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getHaveConnectIpFlag() {
        return haveConnectIpFlag;
    }

    public void setHaveConnectIpFlag(Boolean haveConnectIpFlag) {
        this.haveConnectIpFlag = haveConnectIpFlag;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {

                ntfy("Delete AliEdgeRouterVO")
                        .resource(uuid, AliEdgeRouterVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
