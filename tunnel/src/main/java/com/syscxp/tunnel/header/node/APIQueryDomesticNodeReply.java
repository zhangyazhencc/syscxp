package com.syscxp.tunnel.header.node;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryDomesticNodeReply extends APIQueryReply{
    List<DomesticNodeInventory> domesticNodeInventoryList;

    public List<DomesticNodeInventory> getDomesticNodeInventoryList() {
        return domesticNodeInventoryList;
    }

    public void setDomesticNodeInventoryList(List<DomesticNodeInventory> domesticNodeInventoryList) {
        this.domesticNodeInventoryList = domesticNodeInventoryList;
    }
}
