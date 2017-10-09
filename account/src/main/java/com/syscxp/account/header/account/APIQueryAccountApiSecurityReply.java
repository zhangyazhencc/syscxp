package com.syscxp.account.header.account;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by wangwg on 2017/08/18.
 */
public class APIQueryAccountApiSecurityReply extends APIQueryReply {
    private List<AccountApiSecurityInventory> inventories;

    public List<AccountApiSecurityInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AccountApiSecurityInventory> inventories) {
        this.inventories = inventories;
    }
}
