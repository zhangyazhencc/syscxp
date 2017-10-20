package com.syscxp.account.header.ticket;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/9/26.
 */
@SuppressCredentialCheck
public class APICreateTicketMsg  extends APIMessage {

    @APIParam(maxLength = 128)
    private String ticketTypeCode;

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

    public String getTicketTypeCode() {
        return ticketTypeCode;
    }

    public void setTicketTypeCode(String ticketTypeCode) {
        this.ticketTypeCode = ticketTypeCode;
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
