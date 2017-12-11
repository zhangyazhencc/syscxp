package com.syscxp.header.vpn.agent;


import com.syscxp.header.message.NeedReplyMessage;

import java.util.List;

public class CheckVpnStatusMsg extends NeedReplyMessage {
    private String hostUuid;
    private boolean noStatusCheck;
    private List<String> vpnUuids;

    public boolean isNoStatusCheck() {
        return noStatusCheck;
    }

    public void setNoStatusCheck(boolean noStatusCheck) {
        this.noStatusCheck = noStatusCheck;
    }

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
