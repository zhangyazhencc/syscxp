package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.identity.NoticeWay;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(adminOnly = true, category = AccountConstant.ACTION_CATEGORY, names = {"account_contact"})
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
}
