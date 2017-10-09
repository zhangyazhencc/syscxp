package com.syscxp.account.header.identity;

import com.syscxp.account.header.account.AccountMessage;
import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIDeleteMessage;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

/**
 * Created by frank on 7/9/2015.
 */
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT)
public class APIDeleteRoleMsg extends APIDeleteMessage implements AccountMessage {
    @APIParam(resourceType = RoleVO.class, checkAccount = true, operationTarget = true)
    private String uuid;

    @Override
    public String getAccountUuid() {
        return getSession().getAccountUuid();
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
 
    public static APIDeleteRoleMsg __example__() {
        APIDeleteRoleMsg msg = new APIDeleteRoleMsg();
        msg.setUuid(uuid());
        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Deleting").resource(uuid, RoleVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
