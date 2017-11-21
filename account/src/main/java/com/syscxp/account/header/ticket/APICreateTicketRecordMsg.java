package com.syscxp.account.header.ticket;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressUserCredentialCheck;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/9/26.
 */
@SuppressUserCredentialCheck
@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_TICKET, names = {"create"})
public class APICreateTicketRecordMsg extends APIMessage {

    @APIParam(maxLength = 32)
    private String ticketUuid;

    @APIParam(maxLength = 32)
    private RecordBy recordBy;

    @APIParam(maxLength = 2048)
    private String content;

    @APIParam(maxLength = 32)
    private TicketStatus status;

    public String getTicketUuid() {
        return ticketUuid;
    }

    public void setTicketUuid(String ticketUuid) {
        this.ticketUuid = ticketUuid;
    }

    public RecordBy getRecordBy() {
        return recordBy;
    }

    public void setRecordBy(RecordBy recordBy) {
        this.recordBy = recordBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateTicketRecordEvent) evt).getInventory().getUuid();
                }
                ntfy("Create TicketRecord")
                        .resource(uuid, TicketRecordVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
