package com.syscxp.tunnel.header.host;

/**
 */
public class MonitorHostConnectedContext {
    private MonitorHostInventory inventory;
    private boolean newAddedHost;

    public boolean isNewAddedHost() {
        return newAddedHost;
    }

    public void setNewAddedHost(boolean newAddedHost) {
        this.newAddedHost = newAddedHost;
    }

    public MonitorHostInventory getInventory() {
        return inventory;
    }

    public void setInventory(MonitorHostInventory inventory) {
        this.inventory = inventory;
    }
}
