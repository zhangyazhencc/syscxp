package com.syscxp.account.header.identity;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

import static com.syscxp.utils.CollectionDSL.list;

/**
 * Created by frank on 7/14/2015.
 */
@RestResponse(allTo = "inventories")
public class APIQueryRoleReply extends APIQueryReply {
    private List<RoleInventory> inventories;

    public List<RoleInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<RoleInventory> inventories) {
        this.inventories = inventories;
    }
 
    public static APIQueryRoleReply __example__() {
        APIQueryRoleReply reply = new APIQueryRoleReply();
        RoleInventory inventory = new RoleInventory();
        inventory.setName("testuser");
        inventory.setUuid(uuid());
        inventory.setAccountUuid(uuid());
        reply.setInventories(list(inventory));

        return reply;
    }

}
