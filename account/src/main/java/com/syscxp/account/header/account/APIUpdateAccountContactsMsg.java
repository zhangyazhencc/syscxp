package com.syscxp.account.header.account;

import com.syscxp.account.header.identity.NoticeWay;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {"account"}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, adminOnly = true)
public class APIUpdateAccountContactsMsg extends  APIMessage implements AccountMessage{

    @APIParam(maxLength = 32)
    private String uuid;

    @APIParam(maxLength = 128,required = false)
    private String name;

    @APIParam(maxLength = 36,required = false)
    private String email;

    @APIParam(maxLength = 36,required = false)
    private String phone;

    @APIParam(maxLength = 36,required = false)
    private NoticeWay noticeWay;

    @APIParam(maxLength = 255, required = false)
    private String description;

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public NoticeWay getNoticeWay() {
        return noticeWay;
    }

    public String getDescription() {
        return description;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNoticeWay(NoticeWay noticeWay) {
        this.noticeWay = noticeWay;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update account contact").resource(getAccountUuid(), AccountContactsVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
