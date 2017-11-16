package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIListAliRegionReply extends APIQueryReply {
    private List<AliRegionInventory> aliRegionInventories;


    public List<AliRegionInventory> getAliRegionInventories() {
        return aliRegionInventories;
    }

    public void setAliRegionInventories(List<AliRegionInventory> aliRegionInventories) {
        this.aliRegionInventories = aliRegionInventories;
    }
}
