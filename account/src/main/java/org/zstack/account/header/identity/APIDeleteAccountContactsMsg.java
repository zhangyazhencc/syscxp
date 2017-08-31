package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIDeleteMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;


/**
 * Created by wangwg on 2017/08/21.
 */
@Action(category = AccountConstant.ACTION_CATEGORY, names = {"account_contact"})
public class APIDeleteAccountContactsMsg extends APIDeleteMessage implements AccountMessage{
    @APIParam(resourceType = PermissionVO.class, checkAccount = true, operationTarget = true, successIfResourceNotExisting = true)
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
                ntfy("Deleting").resource(uuid, PermissionVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
