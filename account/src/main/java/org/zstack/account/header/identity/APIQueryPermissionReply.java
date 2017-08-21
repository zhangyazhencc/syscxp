package org.zstack.account.header.identity;

import org.zstack.header.query.APIQueryReply;
import org.zstack.header.rest.RestResponse;

import java.util.List;

import static org.zstack.utils.CollectionDSL.list;

/**
 * Created by wangwg on 2017/08/15.
 */
@RestResponse(allTo = "inventories")
public class APIQueryPermissionReply extends APIQueryReply {
    private List<PermissionInventory> inventories;

    public List<PermissionInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<PermissionInventory> inventories) {
        this.inventories = inventories;
    }
 
    public static APIQueryPermissionReply __example__() {
        APIQueryPermissionReply reply = new APIQueryPermissionReply();
        PermissionInventory inventory = new PermissionInventory();
        inventory.setName("testuser");
        inventory.setUuid(uuid());
        reply.setInventories(list(inventory));

        return reply;
    }

}
