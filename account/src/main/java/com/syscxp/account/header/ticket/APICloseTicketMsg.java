package com.syscxp.account.header.ticket;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/9/26.
 */
@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_TICKET, names = {"update"})
public class APICloseTicketMsg extends APIMessage {

    @APIParam(maxLength = 32, resourceType = TicketVO.class, checkAccount = true, operationTarget = true, required = false)
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
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICloseTicketEvent) evt).getInventory().getUuid();
                }
                ntfy("Close Ticket")
                        .resource(uuid, TicketVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
