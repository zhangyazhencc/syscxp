package org.zstack.account.header.identity;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by wangwg on 2017/08/18.
 */
public class APIQueryAccountExtraInfoReply extends APIQueryReply {
    private List<AccountExtraInfoInventory> inventories;

    public List<AccountExtraInfoInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AccountExtraInfoInventory> inventories) {
        this.inventories = inventories;
    }
}
