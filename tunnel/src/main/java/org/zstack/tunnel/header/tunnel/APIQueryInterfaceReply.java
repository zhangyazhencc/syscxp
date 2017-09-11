package org.zstack.tunnel.header.tunnel;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
public class APIQueryInterfaceReply extends APIQueryReply {
    private List<InterfaceToSwitchPortInventory> inventories;

    public List<InterfaceToSwitchPortInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<InterfaceToSwitchPortInventory> inventories) {
        this.inventories = inventories;
    }
}
