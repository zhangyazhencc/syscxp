package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;
import com.syscxp.header.vpn.vpn.VpnStatus;

public class VpnStatusReply extends MessageReply {
    private boolean connected;
    private VpnStatus currentStatus;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public VpnStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(VpnStatus currentStatus) {
        this.currentStatus = currentStatus;
    }
}
