package org.zstack.account.header.user;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

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
