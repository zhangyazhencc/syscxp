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
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT,names = "update")
public class APIUpdateTicketMsg extends APIMessage {

    @APIParam(maxLength = 128)
    private String uuid;

    @APIParam(maxLength = 128, required = false)
    private String ticketTypeCode;

    @APIParam(maxLength = 2048, required = false)
    private String content;

    @APIParam(maxLength = 32, required = false)
    private String phone;

    @APIParam(maxLength = 32, required = false)
    private String email;

    @APIParam(maxLength = 32, required = false)
    private TicketStatus status;

    @APIParam(maxLength = 32, required = false)
    private String adminUserUuid;

    public String getAdminUserUuid() {
        return adminUserUuid;
    }

    public void setAdminUserUuid(String adminUserUuid) {
        this.adminUserUuid = adminUserUuid;
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

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

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
                    uuid = ((APIUpdateTicketEvent) evt).getInventory().getUuid();
                }
                ntfy("Update Ticket")
                        .resource(uuid, TicketVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
