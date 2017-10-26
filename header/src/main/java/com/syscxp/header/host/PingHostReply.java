package com.syscxp.header.host;

import com.syscxp.header.message.MessageReply;

public class PingHostReply extends MessageReply {
    private boolean connected;
    private String currentHostStatus;
    private boolean noReconnect;

    public boolean isNoReconnect() {
        return noReconnect;
    }

    public void setNoReconnect(boolean noReconnect) {
        this.noReconnect = noReconnect;
    }

    public String getCurrentHostStatus() {
        return currentHostStatus;
    }

    public void setCurrentHostStatus(String currentHostStatus) {
        this.currentHostStatus = currentHostStatus;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
