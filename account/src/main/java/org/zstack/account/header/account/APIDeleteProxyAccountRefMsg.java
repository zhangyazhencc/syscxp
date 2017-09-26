package org.zstack.account.header.account;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIDeleteMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.notification.ApiNotification;


/**
 * Created by wangwg on 2017/09/21.
 */
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT,accountOnly = true)
public class APIDeleteProxyAccountRefMsg extends APIDeleteMessage implements AccountMessage{

    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Deleting").resource(uuid, ProxyAccountRefVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
