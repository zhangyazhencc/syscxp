package org.zstack.header.identity;

import org.zstack.header.message.APIReply;


public class APIGetSessionPolicyReply extends APIReply {

    private boolean validSession;

    private SessionPolicyInventory sessionPolicyInventory;

    public boolean isValidSession() {
        return validSession;
    }

    public void setValidSession(boolean validSession) {
        this.validSession = validSession;
    }

    public SessionPolicyInventory getSessionPolicyInventory() {
        return sessionPolicyInventory;
    }

    public void setSessionPolicyInventory(SessionPolicyInventory sessionPolicyInventory) {
        this.sessionPolicyInventory = sessionPolicyInventory;
    }

}
