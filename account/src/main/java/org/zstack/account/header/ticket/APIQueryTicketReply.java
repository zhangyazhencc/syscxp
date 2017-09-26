package org.zstack.account.header.ticket;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by wangwg on 2017/09/26.
 */
public class APIQueryTicketReply extends APIQueryReply {
    private List<TicketInventory> inventories;

    public List<TicketInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TicketInventory> inventories) {
        this.inventories = inventories;
    }
}
