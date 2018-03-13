package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.AliEdgeRouterConstant;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = AliEdgeRouterConstant.ACTION_CATEGORY, names = {"delete"})
public class APIDeleteAliUserMsg extends APIMessage{

    @APIParam(resourceType = AliUserVO.class)
    private String uuid;
    @APIParam(required = false,maxLength = 32)
    private String accountUuid;
    @APIParam(maxLength = 32)
    private String aliAccountUuid;

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

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {

                ntfy("Delete AliUserVO")
                        .resource(uuid, AliUserVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
