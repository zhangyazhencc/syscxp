package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.identity.NoticeWay;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = AccountConstant.ACTION_CATEGORY, names = {"account_contact"})
public class APIUpdateAccountContactsMsg extends  APIMessage implements AccountMessage{

    @APIParam(maxLength = 32)
    private String targetUuid;

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

    public String getTargetUuid() {
        return targetUuid;
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

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
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
}
