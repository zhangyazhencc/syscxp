package com.syscxp.account.header.identity;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

import static com.syscxp.utils.CollectionDSL.list;

/**
 * Created by wangwg on 2017/11/10.
 */
@RestResponse(allTo = "inventories")
public class APIQueryRolePolicyReply extends APIQueryReply {
    private List<RolePolicyRefInventory> inventories;

    public List<RolePolicyRefInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<RolePolicyRefInventory> inventories) {
        this.inventories = inventories;
    }
 


}
