package org.zstack.account.header.ticket;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/9/26.
 */
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT)
public class APICreateTicketMsg  extends APIMessage {

    @APIParam(maxLength = 128)
    private String type;

    @APIParam(maxLength = 2048)
    private String content;

    @APIParam(maxLength = 32)
    private String phone;

    @APIParam(maxLength = 32)
    private String email;

    @APIParam(maxLength = 32)
    private TicketFrom ticketFrom;

    @APIParam(maxLength = 2048, required = false)
    private String contentExtra;


    public TicketFrom getTicketFrom() {
        return ticketFrom;
    }

    public void setTicketFrom(TicketFrom ticketFrom) {
        this.ticketFrom = ticketFrom;
    }

    public String getContentExtra() {
        return contentExtra;
    }

    public void setContentExtra(String contentExtra) {
        this.contentExtra = contentExtra;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateTicketEvent) evt).getInventory().getUuid();
                }
                ntfy("Create Ticket")
                        .resource(uuid, TicketVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
