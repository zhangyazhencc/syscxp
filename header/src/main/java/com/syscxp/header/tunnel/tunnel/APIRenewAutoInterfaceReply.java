package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

public class APIRenewAutoInterfaceReply extends APIReply {

    private InterfaceInventory inventory;

    private boolean temporaryRenew;

    public boolean isTemporaryRenew() {
        return temporaryRenew;
    }

    public void setTemporaryRenew(boolean temporaryRenew) {
        this.temporaryRenew = temporaryRenew;
    }

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
