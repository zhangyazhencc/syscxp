package com.syscxp.account.header.identity;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;
import com.syscxp.header.identity.StatementEffect;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xing5 on 2016/3/10.
 */
@RestResponse(allTo = "inventory")
public class APICheckApiPermissionReply extends APIReply {
    private Map<String, String> inventory;

    public Map<String, String> getInventory() {
        return inventory;
    }

    public void setInventory(Map<String, String> inventory) {
        this.inventory = inventory;
    }
 
    public static APICheckApiPermissionReply __example__() {
        APICheckApiPermissionReply reply = new APICheckApiPermissionReply();
        Map<String, String> inventory = new HashMap<>();
        inventory.put("APICheckApiPermissionMsg", StatementEffect.Allow.toString());
        reply.setInventory(inventory);
        return reply;
    }

}
