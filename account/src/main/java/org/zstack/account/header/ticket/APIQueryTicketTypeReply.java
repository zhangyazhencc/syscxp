package org.zstack.account.header.ticket;

import org.zstack.account.header.account.AccountInventory;
import org.zstack.header.query.APIQueryReply;

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
