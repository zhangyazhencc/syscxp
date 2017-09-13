package org.zstack.sms.header;

import org.zstack.header.identity.SuppressCredentialCheck;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

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
