package org.zstack.account.header.account;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse
public class APIAccountMailAuthenticationEvent extends APIEvent {

    private String mail;

    public APIAccountMailAuthenticationEvent() {
        super(null);
    }

    public APIAccountMailAuthenticationEvent(String apiId) {
        super(apiId);
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
