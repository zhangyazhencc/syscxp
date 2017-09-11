package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/08/23.
 */
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT)
public class APIUpdateApiAllowIPMsg extends APIMessage implements AccountMessage{

    @APIParam(maxLength = 2048)
    private String allowIP;

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public String getAllowIP() {
        return allowIP;
    }

    public void setAllowIP(String allowIP) {
        this.allowIP = allowIP;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update allow ip").resource(getAccountUuid(), AccountApiSecurityVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
