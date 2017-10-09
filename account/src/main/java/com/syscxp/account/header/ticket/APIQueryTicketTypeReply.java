package com.syscxp.account.header.ticket;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by wangwg on 2017/09/26.
 */
public class APIQueryTicketTypeReply extends APIQueryReply {
    private List<TicketTypeInventory> inventories;

    public List<TicketTypeInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TicketTypeInventory> inventories) {
        this.inventories = inventories;
    }
}
