package com.syscxp.header.account;

import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.message.APIReply;

public class APILogInBySecretIdReply extends APIReply {
    private SessionInventory session;

    public SessionInventory getSession() {
        return session;
    }

    public void setSession(SessionInventory session) {
        this.session = session;
    }
}
