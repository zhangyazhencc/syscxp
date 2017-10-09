package com.syscxp.account.header.account;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by wangeg on 2017/09/26.
 */
public class APIQueryProxyAccountReply extends APIQueryReply {
    private List<ProxyAccountInventory> inventories;

    public List<ProxyAccountInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ProxyAccountInventory> inventories) {
        this.inventories = inventories;
    }
}
