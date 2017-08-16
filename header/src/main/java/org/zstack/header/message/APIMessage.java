package org.zstack.header.message;

import org.zstack.header.identity.SessionInventory;
import org.zstack.header.rest.APINoSee;

public abstract class APIMessage extends NeedReplyMessage {
    /**
     * @ignore
     */
    @NoJsonSchema
    @APINoSee
    private SessionInventory session;

    private String ip;

    public SessionInventory getSession() {
        return session;
    }

    public void setSession(SessionInventory session) {
        this.session = session;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
