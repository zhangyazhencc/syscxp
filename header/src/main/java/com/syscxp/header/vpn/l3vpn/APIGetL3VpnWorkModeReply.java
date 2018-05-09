package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.message.APIReply;

public class APIGetL3VpnWorkModeReply extends APIReply {
    private String workMode;

    public String getWorkMode() { return workMode; }

    public void setWorkMode(String workMode) { this.workMode = workMode; }
}
