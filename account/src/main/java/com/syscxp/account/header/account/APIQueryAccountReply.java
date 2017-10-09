package com.syscxp.account.header.account;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by frank on 7/14/2015.
 */
public class APIQueryAccountReply extends APIQueryReply {
    private List<AccountInventory> inventories;

    public List<AccountInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AccountInventory> inventories) {
        this.inventories = inventories;
    }
}
