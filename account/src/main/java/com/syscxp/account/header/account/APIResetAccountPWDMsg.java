package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, adminOnly = true)
public class APIResetAccountPWDMsg extends  APIMessage implements  AccountMessage {

    @APIParam(maxLength = 32)
    private String uuid;

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public String getUuid() {
        return uuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.uuid = targetUuid;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Reset account password").resource(uuid, AccountVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}

