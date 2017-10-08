package com.syscxp.account.header.account;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse
public class APIAccountPhoneAuthenticationEvent extends APIEvent {

    private String phone;

    public APIAccountPhoneAuthenticationEvent() {
        super(null);
    }

    public APIAccountPhoneAuthenticationEvent(String apiId) {
        super(apiId);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
