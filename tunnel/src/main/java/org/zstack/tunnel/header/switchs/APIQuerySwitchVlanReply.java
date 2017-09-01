package org.zstack.tunnel.header.switchs;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-01
 */
public class APIQuerySwitchVlanReply extends APIQueryReply {
    private List<SwitchVlanSwitchSwitchModelInventory> inventories;

    public List<SwitchVlanSwitchSwitchModelInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchVlanSwitchSwitchModelInventory> inventories) {
        this.inventories = inventories;
    }
}
