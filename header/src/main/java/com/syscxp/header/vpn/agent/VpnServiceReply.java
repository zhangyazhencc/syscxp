package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;
import com.syscxp.header.vpn.vpn.VpnStatus;

public class VpnServiceReply extends MessageReply {
    private VpnStatus status;

    public VpnStatus getStatus() {
        return status;
    }

    public void setStatus(VpnStatus status) {
        this.status = status;
    }
}
