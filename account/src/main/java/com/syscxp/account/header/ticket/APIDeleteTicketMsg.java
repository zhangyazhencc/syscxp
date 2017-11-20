package com.syscxp.account.header.ticket;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.user.UserVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.identity.SuppressUserCredentialCheck;
import com.syscxp.header.message.APIDeleteMessage;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;


/**
 * Created by wangwg on 2017/09/26.
 */
@SuppressUserCredentialCheck
@Action(services = {"account"}, category = AccountConstant.ACTION_CATEGORY_TICKET, names = {"delete"})
public class APIDeleteTicketMsg extends APIDeleteMessage {

    @APIParam(resourceType = TicketVO.class, checkAccount = true, operationTarget = true, required = false)
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
                ntfy("Deleting").resource(uuid, TicketVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
