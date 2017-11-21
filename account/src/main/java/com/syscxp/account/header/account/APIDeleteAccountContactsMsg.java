package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIDeleteMessage;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.notification.ApiNotification;


/**
 * Created by wangwg on 2017/08/21.
 */
@Action(services = {AccountConstant.ACTION_SERVICE}, adminOnly = true, category = AccountConstant.ACTION_CATEGORY_ACCOUNT)
public class APIDeleteAccountContactsMsg extends APIDeleteMessage implements AccountMessage{

    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
 
    public static APIDeleteAccountContactsMsg __example__() {
        APIDeleteAccountContactsMsg msg = new APIDeleteAccountContactsMsg();
        msg.setUuid(uuid());
        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Deleting").resource(uuid, AccountContactsVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
