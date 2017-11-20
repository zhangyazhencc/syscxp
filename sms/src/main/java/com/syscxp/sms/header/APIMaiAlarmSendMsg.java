package com.syscxp.sms.header;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.util.List;

@InnerCredentialCheck
public class APIMaiAlarmSendMsg extends APIMessage{

    @APIParam
    private List<String> emails;

    @APIParam
    private String subject;

    @APIParam
    private String comtent;

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getComtent() {
        return comtent;
    }

    public void setComtent(String comtent) {
        this.comtent = comtent;
    }
}
