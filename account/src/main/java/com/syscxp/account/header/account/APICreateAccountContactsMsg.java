package com.syscxp.account.header.account;

import com.syscxp.account.header.identity.NoticeWay;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(adminOnly = true, category = AccountConstant.ACTION_CATEGORY_ACCOUNT)
public class APICreateAccountContactsMsg extends  APIMessage implements  AccountMessage {

    @APIParam(maxLength = 32)
    private String accountUuid;
    @APIParam(maxLength = 128)
    private String name;
    @APIParam(maxLength = 36,required = false)
    private String phone;
    @APIParam(maxLength = 36,required = false)
    private String email;
    @APIParam(maxLength = 36)
    private NoticeWay noticeWay;

    @APIParam(required = false)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
