package com.syscxp.account.header.user;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse
public class APIAttachPolicyToUserEvent extends APIEvent {
    private UserRoleRefVO upv;

    public APIAttachPolicyToUserEvent() {
        super(null);
    }

    public APIAttachPolicyToUserEvent(String apiId) {
        super(apiId);
    }

    public UserRoleRefVO getUpv() {
        return upv;
    }

    public void setUpv(UserRoleRefVO upv) {
        this.upv = upv;
    }
}
