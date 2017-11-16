package com.syscxp.sms.header;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
@InnerCredentialCheck
public class APIMaiAlarmSendMsg extends APIMessage{

    @APIParam
    private String email;

    @APIParam
    private String subject;

    @APIParam
    private String comtent;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
