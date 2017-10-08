package com.syscxp.header.managementnode;

import com.syscxp.header.message.MessageReply;

/**
 */
public class IsManagementNodeReadyReply extends MessageReply {
    private boolean ready;

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
