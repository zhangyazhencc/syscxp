package org.zstack.account.header.identity;

import org.zstack.header.query.APIQueryReply;
import org.zstack.header.rest.RestResponse;

import java.util.List;

import static org.zstack.utils.CollectionDSL.list;

/**
 * Created by wangwg on 2017/08/15.
 */
@RestResponse(allTo = "inventories")
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
