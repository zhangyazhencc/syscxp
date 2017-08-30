package org.zstack.tunnel.header.host;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-08-30
 */
public class APIQueryHostSwitchMonitorReply extends APIQueryReply {
    private List<HostSwitchMonitorInventory> inventories;

    public List<HostSwitchMonitorInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<HostSwitchMonitorInventory> inventories) {
        this.inventories = inventories;
    }
}
