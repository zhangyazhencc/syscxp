package com.syscxp.account.header.identity;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

import static com.syscxp.utils.CollectionDSL.list;

/**
 * Created by wangwg on 2017/08/15.
 */
public class APIQueryPolicyReply extends APIQueryReply {
    private List<PolicyInventory> inventories;

    public List<PolicyInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<PolicyInventory> inventories) {
        this.inventories = inventories;
    }
 
    public static APIQueryPolicyReply __example__() {
        APIQueryPolicyReply reply = new APIQueryPolicyReply();
        PolicyInventory inventory = new PolicyInventory();
        inventory.setName("testuser");
        inventory.setUuid(uuid());
        reply.setInventories(list(inventory));

        return reply;
    }

}
