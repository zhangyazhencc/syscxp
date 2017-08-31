package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.identity.NoticeWay;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = AccountConstant.ACTION_CATEGORY, names = {"account_contact"})
public class APICreateAccountContactsMsg extends  APIMessage implements  AccountMessage {

    @APIParam(maxLength = 32)
    private String targetUuid;
    @APIParam(maxLength = 128)
    public String contacts;
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

    public String getContacts() {
        return contacts;
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

    public void setContacts(String contacts) {
        this.contacts = contacts;
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
}
