package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"update"}, adminOnly = true)
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

