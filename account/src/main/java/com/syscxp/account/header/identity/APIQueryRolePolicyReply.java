package com.syscxp.account.header.identity;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by wangwg on 2017/11/10.
 */
public class APIQueryRolePolicyReply extends APIQueryReply {
    private List<RolePolicyRefInventory> inventories;

    public List<RolePolicyRefInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<RolePolicyRefInventory> inventories) {
        this.inventories = inventories;
    }
 


}
