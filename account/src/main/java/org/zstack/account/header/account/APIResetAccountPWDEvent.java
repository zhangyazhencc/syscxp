package org.zstack.account.header.account;

import org.zstack.header.message.APIEvent;

public class APIResetAccountPWDEvent extends APIEvent {
    private String password;

    public APIResetAccountPWDEvent(String apiId) {
        super(apiId);
    }

    public APIResetAccountPWDEvent() {
        super(null);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
