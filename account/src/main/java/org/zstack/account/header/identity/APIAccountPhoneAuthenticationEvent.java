package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

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
