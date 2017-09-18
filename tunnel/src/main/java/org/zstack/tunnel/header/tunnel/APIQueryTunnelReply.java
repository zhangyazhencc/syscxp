package org.zstack.tunnel.header.tunnel;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-17
 */
public class APIQueryTunnelReply extends APIQueryReply {
    private List<TunnelToNetWorkAndSwitchPortInventory> inventories;

    public List<TunnelToNetWorkAndSwitchPortInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelToNetWorkAndSwitchPortInventory> inventories) {
        this.inventories = inventories;
    }
}
