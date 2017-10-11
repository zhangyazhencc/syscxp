package com.syscxp.account.header.user;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse
public class APIUserPhoneAuthenticationEvent extends APIEvent {

    private String phone;

    public APIUserPhoneAuthenticationEvent() {
        super(null);
    }

    public APIUserPhoneAuthenticationEvent(String apiId) {
        super(apiId);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}