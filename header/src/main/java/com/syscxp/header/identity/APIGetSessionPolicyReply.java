package com.syscxp.header.identity;

import com.syscxp.header.message.APIReply;


public class APIGetSessionPolicyReply extends APIReply {

    private boolean validSession;

    private SessionInventory sessionInventory;

    public boolean isValidSession() {
        return validSession;
    }

    public void setValidSession(boolean validSession) {
        this.validSession = validSession;
    }

    public SessionInventory getSessionInventory() {
        return sessionInventory;
    }

    public void setSessionInventory(SessionInventory sessionInventory) {
        this.sessionInventory = sessionInventory;
    }
}
