package org.zstack.account.header.user;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

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
