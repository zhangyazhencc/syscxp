package com.syscxp.header.vpn.agent;


import com.syscxp.header.message.NeedReplyMessage;

import java.util.List;

public class CheckVpnStatusMsg extends NeedReplyMessage {
    private String hostUuid;
    private List<String> vpnUuids;

    public List<String> getVpnUuids() {
        return vpnUuids;
    }

    public void setVpnUuids(List<String> vpnUuids) {
        this.vpnUuids = vpnUuids;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }
}
