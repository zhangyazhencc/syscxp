package com.syscxp.header.host;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventories")
public class APIQueryHostReply extends APIQueryReply {
    private List<HostInventory> inventories;

    public List<HostInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<HostInventory> inventories) {
        this.inventories = inventories;
    }
 
    public static APIQueryHostReply __example__() {
        APIQueryHostReply reply = new APIQueryHostReply();


        return reply;
    }

}
