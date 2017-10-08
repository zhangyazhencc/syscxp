package com.syscxp.account.header.account;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

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
