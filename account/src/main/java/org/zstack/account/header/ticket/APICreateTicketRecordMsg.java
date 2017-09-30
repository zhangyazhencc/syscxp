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
                        .resource(uuid, TicketRecordVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
