package org.zstack.tunnel.header.monitor;

import org.zstack.header.query.APIQueryReply;
import org.zstack.tunnel.header.host.HostInventory;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
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
