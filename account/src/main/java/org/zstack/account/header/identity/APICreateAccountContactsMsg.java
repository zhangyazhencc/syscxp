package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

@Action(adminOnly = true, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"create"})
public class APICreateAccountContactsMsg extends  APIMessage implements  AccountMessage {

    @APIParam(maxLength = 32)
    private String accountUuid;
    @APIParam(maxLength = 128)
    public String name;
    @APIParam(maxLength = 36)
    public String phone;
    @APIParam(maxLength = 36)
    public String email;
    @APIParam(maxLength = 36)
    public NoticeWay noticeWay;

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

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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
