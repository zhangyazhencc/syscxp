package com.syscxp.tunnel.header.node;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryAbroadNodeReply extends APIQueryReply {
    List<AbroadNodeInventory> abroadNodeInventories;

    public List<AbroadNodeInventory> getAbroadNodeInventories() {
        return abroadNodeInventories;
    }

    public void setAbroadNodeInventories(List<AbroadNodeInventory> abroadNodeInventories) {
        this.abroadNodeInventories = abroadNodeInventories;
    }
}
