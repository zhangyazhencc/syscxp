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
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT,names = "create")
public class APICreateTicketRecordMsg extends APIMessage {

    @APIParam(maxLength = 32)
    private String ticketUuid;

    @APIParam(maxLength = 32)
    private BelongTo belongto;

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

    public BelongTo getBelongto() {
        return belongto;
    }

    public void setBelongto(BelongTo belongto) {
        this.belongto = belongto;
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
                    uuid = ((APICreateTicketEvent) evt).getInventory().getUuid();
                }
                ntfy("Create TicketRecord")
                        .resource(uuid, TicketVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
