package com.syscxp.account.header.user;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

/**
 * Created by frank on 7/14/2015.
 */
@RestResponse(allTo = "inventories")
public class APIQueryUserReply extends APIQueryReply {
    private List<UserInventory> inventories;

    public List<UserInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<UserInventory> inventories) {
        this.inventories = inventories;
    }
 

}
