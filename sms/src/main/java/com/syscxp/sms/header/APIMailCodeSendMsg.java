package com.syscxp.sms.header;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@SuppressCredentialCheck
public class APIMailCodeSendMsg extends APISyncCallMessage {

    @APIParam(nonempty = true)
    private String mail;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
    
}
