package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;

import java.util.List;

/**
 * Create by DCY on 2018/5/9
 */
public class APIListTEConfigReply extends APIReply {

    private List<TEConfigInventory> inventories;

    public List<TEConfigInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TEConfigInventory> inventories) {
        this.inventories = inventories;
    }
}
