package org.zstack.account.header.ticket;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.account.header.account.AccountGrade;
import org.zstack.account.header.account.AccountMessage;
import org.zstack.account.header.account.AccountVO;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT,names = "create")
public class APICreateTicketMsg  extends APIMessage implements TicketMessage{

    @APIParam(maxLength = 128)
    private String type;

    @APIParam(maxLength = 128)
    private String content;

    @APIParam(maxLength = 32)
    private String phone;

    @APIParam(maxLength = 32)
    private String email;

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
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
                ntfy("Create Account")
                        .resource(uuid, TicketVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
