package com.syscxp.account.header.account;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;


/**
 * Created by wangwg on 2017/08/21.
 */
@RestResponse(allTo = "inventories")
public class APIQueryAccountContactsReply extends APIQueryReply {
    private List<AccountContactsInventory> inventories;

    public List<AccountContactsInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AccountContactsInventory> inventories) {
        this.inventories = inventories;
    }

}
