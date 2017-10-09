package com.syscxp.account.header.ticket;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by wangwg on 2017/09/26.
 */
public class APIQueryTicketRecordReply extends APIQueryReply {
    private List<TicketRecordInventory> inventories;

    public List<TicketRecordInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TicketRecordInventory> inventories) {
        this.inventories = inventories;
    }
}
