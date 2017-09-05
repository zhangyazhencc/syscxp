package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.identity.NoticeWay;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

@Action(category = AccountConstant.ACTION_CATEGORY, names = {"account_contact"})
public class APICreateAccountContactsMsg extends  APIMessage implements  AccountMessage {

    @APIParam(maxLength = 32)
    private String targetUuid;
    @APIParam(maxLength = 128)
    public String name;
    @APIParam(maxLength = 36)
    public String phone;
    @APIParam(maxLength = 36)
    public String email;
    @APIParam(maxLength = 36)
    public NoticeWay noticeWay;

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public NoticeWay getNoticeWay() {
        return noticeWay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNoticeWay(NoticeWay noticeWay) {
        this.noticeWay = noticeWay;
    }

    public String getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateAccountContactsEvent) evt).getInventory().getUuid();
                }
                ntfy("Create Account")
                        .resource(uuid, AccountContactsVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
