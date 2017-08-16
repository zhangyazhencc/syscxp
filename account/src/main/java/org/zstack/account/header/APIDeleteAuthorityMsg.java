package org.zstack.account.header;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIDeleteMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;


/**
 * Created by wangwg on 2017/08/15.
 */
@Action(category = AccountConstant.ACTION_CATEGORY, accountOnly = true)
public class APIDeleteAuthorityMsg extends APIDeleteMessage {
    @APIParam(resourceType = AuthorityVO.class, checkAccount = true, operationTarget = true, successIfResourceNotExisting = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
 
    public static APIDeleteAuthorityMsg __example__() {
        APIDeleteAuthorityMsg msg = new APIDeleteAuthorityMsg();
        msg.setUuid(uuid());
        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Deleting").resource(uuid, AuthorityVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
