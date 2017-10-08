package com.syscxp.header.managementnode;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.sql.Timestamp;
import java.util.List;

import static com.syscxp.utils.CollectionDSL.list;

/**
 */
@RestResponse(allTo = "inventories")
public class APIQueryManagementNodeReply extends APIQueryReply {
    private List<ManagementNodeInventory> inventories;

    public List<ManagementNodeInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ManagementNodeInventory> inventories) {
        this.inventories = inventories;
    }
 
    public static APIQueryManagementNodeReply __example__() {
        APIQueryManagementNodeReply reply = new APIQueryManagementNodeReply();
        ManagementNodeInventory inventory = new ManagementNodeInventory();
        inventory.setUuid(uuid());
        inventory.setHostName("127.0.0.1");
        inventory.setJoinDate(new Timestamp(System.currentTimeMillis()));
        inventory.setHeartBeat(new Timestamp(System.currentTimeMillis()));

        reply.setInventories(list(inventory));
        return reply;
    }

}
