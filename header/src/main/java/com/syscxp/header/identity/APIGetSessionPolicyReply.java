package com.syscxp.header.identity;

import com.syscxp.header.message.APIReply;


public class APIGetSessionPolicyReply extends APIReply {

    private boolean validSession;

    private String accountName;

    private SessionInventory sessionInventory;

    public boolean isValidSession() {
        return validSession;
    }

    public void setValidSession(boolean validSession) {
        this.validSession = validSession;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public SessionInventory getSessionInventory() {
        return sessionInventory;
    }

    public void setSessionInventory(SessionInventory sessionInventory) {
        this.sessionInventory = sessionInventory;
    }
}
