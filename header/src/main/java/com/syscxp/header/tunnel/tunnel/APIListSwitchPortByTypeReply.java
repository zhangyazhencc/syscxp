package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.tunnel.switchs.SwitchPortInventory;

import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
public class APIListSwitchPortByTypeReply extends APIReply {
    private List<SwitchPortInventory> inventories;

    public List<SwitchPortInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchPortInventory> inventories) {
        this.inventories = inventories;
    }
}
