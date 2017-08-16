package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse
public class APIAttachPolicyToUserEvent extends APIEvent {
    private UserPolicyRefVO upv;

    public APIAttachPolicyToUserEvent() {
        super(null);
    }

    public APIAttachPolicyToUserEvent(String apiId) {
        super(apiId);
    }

    public UserPolicyRefVO getUpv() {
        return upv;
    }

    public void setUpv(UserPolicyRefVO upv) {
        this.upv = upv;
    }
}
