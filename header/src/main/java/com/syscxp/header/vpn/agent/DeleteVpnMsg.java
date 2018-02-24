package com.syscxp.header.vpn.agent;


import com.syscxp.header.message.NeedReplyMessage;

public class DeleteVpnMsg extends NeedReplyMessage {
    private boolean deleteRenew;
    private String vpnUuid;

    public boolean isDeleteRenew() {
        return deleteRenew;
    }

    public void setDeleteRenew(boolean deleteRenew) {
        this.deleteRenew = deleteRenew;
    }

    public String getVpnUuid() {
        return vpnUuid;
    }

    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }
}
