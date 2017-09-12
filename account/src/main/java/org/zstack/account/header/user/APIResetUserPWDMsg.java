package org.zstack.account.header.user;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.account.header.account.AccountMessage;
import org.zstack.account.header.account.AccountVO;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

@Action(category = AccountConstant.ACTION_CATEGORY_USER)
public class APIResetUserPWDMsg extends  APIMessage implements AccountMessage {

    @APIParam(maxLength = 32)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
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

