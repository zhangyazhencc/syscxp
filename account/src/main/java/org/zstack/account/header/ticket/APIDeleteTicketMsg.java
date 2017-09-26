package org.zstack.account.header.ticket;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIDeleteMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.notification.ApiNotification;


/**
 * Created by wangwg on 2017/09/26.
 */
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT,names = "delete")
public class APIDeleteTicketMsg extends APIDeleteMessage {

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
                ntfy("Deleting").resource(uuid, TicketVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
