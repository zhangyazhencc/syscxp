package com.syscxp.account.header.user;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse
public class APIUserMailAuthenticationEvent extends APIEvent {

    private String mail;

    public APIUserMailAuthenticationEvent() {
        super(null);
    }

    public APIUserMailAuthenticationEvent(String apiId) {
        super(apiId);
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
