package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/08/23.
 */
@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT)
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
                ntfy("Update allow ip").resource(getAccountUuid(), AccountApiSecurityVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
