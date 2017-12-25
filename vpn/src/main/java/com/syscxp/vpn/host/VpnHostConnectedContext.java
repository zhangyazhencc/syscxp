package com.syscxp.vpn.host;

import com.syscxp.header.vpn.host.VpnHostInventory;

/**
 * @author wangjie
 */
public class VpnHostConnectedContext {
    private VpnHostInventory inventory;
    private boolean newAddedHost;

    public boolean isNewAddedHost() {
        return newAddedHost;
    }

    public void setNewAddedHost(boolean newAddedHost) {
        this.newAddedHost = newAddedHost;
    }

    public VpnHostInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnHostInventory inventory) {
        this.inventory = inventory;
    }
}
