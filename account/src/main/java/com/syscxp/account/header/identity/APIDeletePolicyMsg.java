package com.syscxp.account.header.identity;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.account.AccountMessage;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIDeleteMessage;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;


/**
 * Created by wangwg on 2017/08/15.
 */
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"delete"}, adminOnly = true)
public class APIDeletePolicyMsg extends APIDeleteMessage implements AccountMessage {
    @APIParam(resourceType = PolicyVO.class, checkAccount = true, operationTarget = true, successIfResourceNotExisting = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
 
    public static APIDeletePolicyMsg __example__() {
        APIDeletePolicyMsg msg = new APIDeletePolicyMsg();
        msg.setUuid(uuid());
        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Deleting PolicyVO").resource(uuid, PolicyVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
