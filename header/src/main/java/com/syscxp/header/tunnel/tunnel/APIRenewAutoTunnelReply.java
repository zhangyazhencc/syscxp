package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;

/**
 * Create by DCY on 2017/11/15
 */
public class APIRenewAutoTunnelReply extends APIReply {
    private TunnelInventory inventory;

    private boolean temporaryRenew;

    public boolean isTemporaryRenew() {
        return temporaryRenew;
    }

    public void setTemporaryRenew(boolean temporaryRenew) {
        this.temporaryRenew = temporaryRenew;
    }

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
