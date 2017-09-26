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

    private String refId;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }


    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Deleting").resource(refId, ProxyAccountRefVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
