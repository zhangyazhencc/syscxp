package org.zstack.account.header;

import org.zstack.header.query.APIQueryReply;
import org.zstack.header.rest.RestResponse;

import java.util.List;

import static org.zstack.utils.CollectionDSL.list;

/**
 * Created by wangwg on 2017/08/15.
 */
@RestResponse(allTo = "inventories")
public class APIQueryAuthorityReply extends APIQueryReply {
    private List<AuthorityInventory> inventories;

    public List<AuthorityInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AuthorityInventory> inventories) {
        this.inventories = inventories;
    }
 
    public static APIQueryAuthorityReply __example__() {
        APIQueryAuthorityReply reply = new APIQueryAuthorityReply();
        AuthorityInventory inventory = new AuthorityInventory();
        inventory.setName("testuser");
        inventory.setUuid(uuid());
        reply.setInventories(list(inventory));

        return reply;
    }

}
