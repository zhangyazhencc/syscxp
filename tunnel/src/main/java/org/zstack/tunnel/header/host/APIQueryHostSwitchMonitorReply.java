package org.zstack.tunnel.header.host;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-08-30
 */
public class APIQueryHostSwitchMonitorReply extends APIQueryReply {
    private List<HostSwitchPortInventory> inventories;

    public List<HostSwitchPortInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<HostSwitchPortInventory> inventories) {
        this.inventories = inventories;
    }
}
