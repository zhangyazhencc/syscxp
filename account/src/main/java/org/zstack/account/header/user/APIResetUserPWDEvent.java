package org.zstack.account.header.user;

import org.zstack.header.message.APIEvent;

public class APIResetUserPWDEvent extends APIEvent {
    private String password;

    public APIResetUserPWDEvent(String apiId) {
        super(apiId);
    }

    public APIResetUserPWDEvent() {
        super(null);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
