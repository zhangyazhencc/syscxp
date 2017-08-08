package org.zstack.account.header.identity;

import org.zstack.header.identity.SessionInventory;
import org.zstack.header.message.APIReply;

/**
 *
 * Created by wangwg on 2017/8/5.
 */
public class APIProvingSessionReply extends APIReply {
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

    public static APIProvingSessionReply __example__() {
        APIProvingSessionReply reply = new APIProvingSessionReply();
        reply.setValidSession(true);
        return reply;
    }

}
